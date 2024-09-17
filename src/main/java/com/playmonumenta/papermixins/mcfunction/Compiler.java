package com.playmonumenta.papermixins.mcfunction;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.codegen.DebugCodeGenerator;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import com.playmonumenta.papermixins.mcfunction.parse.parser.Parser;
import com.playmonumenta.papermixins.mcfunction.parse.reader.ExpandedFunctionSource;
import com.playmonumenta.papermixins.mcfunction.parse.reader.RawFunctionSource;
import static com.playmonumenta.papermixins.util.Util.coalesce;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.commands.functions.PlainTextFunction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Compiler {
    private static final Logger LOGGER = MonumentaMod.getLogger("FunctionCompiler");
    private static final FunctionInstantiationException INSTANTIATION_EXCEPTION =
        new FunctionInstantiationException(Component.literal("failed to parse instantiated"));

    private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("#"), format -> {
        format.setMaximumFractionDigits(15);
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
    });

    private static PlainTextFunction<CommandSourceStack> compileImpl(ResourceLocation id, CompileContext context,
                                                                     ExpandedFunctionSource source) {
        final var parser = new Parser(context, source);
        final var ast = parser.parseTopLevel();

        if (context.diagnostics().hasError()) {
            context.diagnostics().dumpErrors(2, LOGGER, id, source.rawSource());
            return null;
        }

        final var codegenContext = new CodegenContext();

        final var shouldDebugDump = source.featureSet().isDebugDump();

        final CodeGenerator<CommandSourceStack> codegen = shouldDebugDump ?
            new DebugCodeGenerator<>() :
            new CodeGenerator<>();

        ast.emit(context.diagnostics(), codegenContext, codegen);


        if (shouldDebugDump) {
            LOGGER.info("AST dump: \n{}\nCodegen dump: \n{}\n----", ast.dump(), codegen.dumpDisassembly());
        }

        context.diagnostics().dumpErrors(2, LOGGER, id, source.rawSource());

        if (context.diagnostics().hasError()) {
            return null;
        }

        return codegen.define(id);
    }

    private static CommandFunction<CommandSourceStack> compileMacro(ResourceLocation id,
                                                                    RawFunctionSource rawFunctionSource) {
        return new CommandFunction<>() {
            @Override
            public @NotNull ResourceLocation id() {
                return id;
            }

            @Override
            public @NotNull InstantiatedFunction<CommandSourceStack> instantiate(
                @Nullable CompoundTag arguments,
                @NotNull CommandDispatcher<CommandSourceStack> dispatcher,
                @NotNull CommandSourceStack source
            ) throws FunctionInstantiationException {
                final var context = new CompileContext(new Diagnostics(), dispatcher, source);

                final var macroArgs = coalesce(arguments, CompoundTag::new).tags.entrySet()
                    .stream()
                    .collect(Collectors.<Map.Entry<String, Tag>, String, String>toMap(
                        Map.Entry::getKey,
                        entry -> {
                            final var nbt = entry.getValue();
                            if (nbt instanceof FloatTag floatTag) {
                                return DECIMAL_FORMAT.format(floatTag.getAsFloat());
                            } else if (nbt instanceof DoubleTag doubleTag) {
                                return DECIMAL_FORMAT.format(doubleTag.getAsDouble());
                            } else if (nbt instanceof ByteTag byteTag) {
                                return String.valueOf(byteTag.getAsByte());
                            } else if (nbt instanceof ShortTag shortTag) {
                                return String.valueOf(shortTag.getAsShort());
                            } else if (nbt instanceof LongTag longTag) {
                                return String.valueOf(longTag.getAsLong());
                            } else {
                                return nbt.getAsString();
                            }
                        }
                    ));

                final var res = rawFunctionSource.instantiate(context, macroArgs)
                    .map(x -> compileImpl(id, context, x))
                    .orElseThrow(() -> INSTANTIATION_EXCEPTION);

                context.diagnostics().dumpErrors(2, LOGGER, id, rawFunctionSource.rawSource());

                if (context.diagnostics().hasError()) {
                    throw INSTANTIATION_EXCEPTION;
                }

                return res;
            }
        };
    }

    /**
     * Parses a command function with extended syntax capabilities.
     *
     * @param dispatcher  The instance of minecraft's dispatcher.
     * @param dummySource A dummy command source.
     * @param lines       The source code.
     * @param id          The resource location of the function.
     * @return The parsed command function, or null if error.
     */
    @Nullable
    public static CommandFunction<CommandSourceStack> compileFunction(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandSourceStack dummySource, List<String> lines, ResourceLocation id) {

        final var context = new CompileContext(new Diagnostics(), dispatcher, dummySource);
        final var reader = RawFunctionSource.fromLines(context, lines);

        if (reader.hasMacro()) {
            if (context.diagnostics().hasError()) {
                context.diagnostics().dumpErrors(MonumentaMod.getConfig().mcFunction.diagnosticContext, LOGGER, id,
                    lines);
                return null;
            }

            return compileMacro(id, reader);
        } else {
            // compile it immediately!
            return reader.instantiate(context, Map.of())
                .map(x -> compileImpl(id, context, x))
                .orElse(null);
        }
    }
}
