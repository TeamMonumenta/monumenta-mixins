package com.playmonumenta.papermixins.mcfunction;

import static com.playmonumenta.papermixins.util.Util.coalesce;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.codegen.DebugCodeGenerator;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import com.playmonumenta.papermixins.mcfunction.parse.parser.Parser;
import com.playmonumenta.papermixins.mcfunction.parse.reader.ExpandedFunctionSource;
import com.playmonumenta.papermixins.mcfunction.parse.reader.RawFunctionSource;
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
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Compiler {
	private static final Logger LOGGER = MonumentaMod.getLogger("FunctionCompiler");
	private static final FunctionInstantiationException INSTANTIATION_EXCEPTION =
		new FunctionInstantiationException(Component.literal("failed to parse instantiated"));

	private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("#"), format -> {
		format.setMaximumFractionDigits(15);
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
	});

	private static PlainTextFunction<CommandSourceStack> compileImpl(ResourceLocation id, String pack,
																	CompileContext context,
																	ExpandedFunctionSource source) {
		final var parser = new Parser(context, source);
		final var ast = parser.parseTopLevel();

		if (context.diagnostics().hasError()) {
			context.diagnostics().dumpErrors(LOGGER, pack, id, source.rawSource());
			return null;
		}

		final var codegenContext = new CodegenContext();

		final var shouldDebugDump = source.featureSet().isDebugDump();

		final CodeGenerator<CommandSourceStack> codegen = shouldDebugDump ?
			new DebugCodeGenerator<>() :
			new CodeGenerator<>();

		ast.emit(context.diagnostics(), codegenContext, codegen);

		context.diagnostics().dumpErrors(LOGGER, pack, id, source.rawSource());

		if (context.diagnostics().hasError()) {
			return null;
		}

		final var res = codegen.define(id);

		if (shouldDebugDump) {
			LOGGER.info("AST dump: \n{}\nCodegen dump: \n{}\n----", ast.dump(), codegen.dumpDisassembly());
		}

		return res;
	}

	private static CommandFunction<CommandSourceStack> compileMacro(ResourceLocation id, String pack, RawFunctionSource rawFunctionSource) {
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
					.map(x -> compileImpl(id, pack, context, x))
					.orElseThrow(() -> INSTANTIATION_EXCEPTION);

				context.diagnostics().dumpErrors(LOGGER,pack,  id, rawFunctionSource.rawSource());

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
		CommandSourceStack dummySource, List<String> lines, ResourceLocation id, String pack) {

		final var context = new CompileContext(new Diagnostics(), dispatcher, dummySource);
		final var reader = RawFunctionSource.fromLines(context, lines);

		if (reader.hasMacro()) {
			if (context.diagnostics().hasError()) {
				context.diagnostics().dumpErrors(LOGGER, pack, id, lines);
				return null;
			}

			return compileMacro(id, pack, reader);
		} else {
			// compile it immediately!
			return reader.instantiate(context, Map.of())
				.map(x -> compileImpl(id, pack, context, x))
				.orElse(null);
		}
	}
}
