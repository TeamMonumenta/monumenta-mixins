package com.playmonumenta.papermixins.mcfunction.parse.reader;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.playmonumenta.papermixins.mcfunction.CompileContext;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ParseFeatureSet;
import com.playmonumenta.papermixins.mcfunction.parse.parser.Parser;
import com.playmonumenta.papermixins.util.CommandUtil;
import com.playmonumenta.papermixins.util.ComponentUtils;
import com.playmonumenta.papermixins.util.IdAllocator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.commands.functions.StringTemplate;

public class RawFunctionSource {
    public static final String WARN_EMPTY_PRAGMA = "empty pragma, consider removing it";
    public static final String WARN_EMPTY_MACRO = "empty macro, consider removing it";
    public static final String ERR_FORWARD_SLASH = "unknown or invalid command '%s' (do you mean '%s'?)";
    public static final String ERR_FAILED_PARSE = "failed to parse command: '%s'";
    public static final String ERR_FAILED_PARSE_PRAGMA = "failed to parse pragma: '%s'";
    public static final String ERR_BAD_COMMENT = "'//' is not valid syntax for comments, use '#' instead";
    public static final String ERR_BAD_PRAGMA = "pragma is not allowed after function body has begun";
    public static final String ERR_MISSING_MACRO = "missing macro argument named '%s'";
    public static final DynamicCommandExceptionType PRAGMA_BAD_FEAT =
        CommandUtil.exceptionType(s -> ComponentUtils.fLiteral("unknown pragma feature flag %s", s));

    private static final CommandDispatcher<ParseFeatureSet> PRAGMA_DISPATCH = new CommandDispatcher<>();

    static {
        PRAGMA_DISPATCH.register(CommandUtil.lit("pragma", CommandUtil.lit("enable", CommandUtil.arg("flag",
            StringArgumentType.word(), (context) -> {
                final var value = StringArgumentType.getString(context, "flag");
                if (context.getSource().enable(StringArgumentType.getString(context, "flag"))) {
                    throw PRAGMA_BAD_FEAT.create(value);
                }
                return 0;
            })), CommandUtil.lit("disable", CommandUtil.arg("flag", StringArgumentType.word(), (context) -> {
            final var value = StringArgumentType.getString(context, "flag");
            if (context.getSource().disable(StringArgumentType.getString(context, "flag"))) {
                throw PRAGMA_BAD_FEAT.create(value);
            }
            return 0;
        }))));
    }

    private final List<MCFunctionLine> codeEntries;
    private final IdAllocator<String> macroArgumentAllocator;
    private final ParseFeatureSet featureSet;
    private final List<String> rawSource;

    private RawFunctionSource(List<MCFunctionLine> codeEntries, IdAllocator<String> macroArgumentAllocator,
                              ParseFeatureSet featureSet, List<String> rawSource) {
        this.codeEntries = codeEntries;
        this.macroArgumentAllocator = macroArgumentAllocator;
        this.featureSet = featureSet;
        this.rawSource = rawSource;
    }

    private static boolean handleLineDiagnostics(Diagnostics context, String line, int index) {
        if (line.isEmpty() || line.startsWith("#")) {
            return true;
        }

        if (line.startsWith("//")) {
            context.reportErr(index, ERR_BAD_COMMENT);
            return true;
        }

        if (line.startsWith("/")) {
            context.reportErr(index, ERR_FORWARD_SLASH, line.substring(1));
            return true;
        }

        return false;
    }

    private static void handleSingleLine(CompileContext context, String line, List<MCFunctionLine> codeEntries,
                                         int index) {
        final var parts = line.split(" ", 2);

        if (Parser.controlFlowKeywords().contains(parts[0])) {
            codeEntries.add(MCFunctionLine.controlFlow(index, parts[0], line));
        } else {
            final var res = CommandUtil.parseCommand(
                context.dispatcher(), context.dummy(), new StringReader(line),
                msg -> context.diagnostics().reportErr(index, ERR_FAILED_PARSE, msg)
            );

            res.ifPresent(action -> codeEntries.add(MCFunctionLine.preParsed(index, action)));
        }
    }

    public static RawFunctionSource fromLines(CompileContext context, List<String> lines) {
        int index = 0;
        final var macroArgumentAllocator = new IdAllocator<String>();
        final var parseFeatureSet = new ParseFeatureSet();
        final var codeEntries = new ArrayList<MCFunctionLine>();

        for (; index < lines.size(); index++) {
            final var line = lines.get(index).trim();

            if (line.startsWith("!")) {
                if (line.equals("!")) {
                    context.diagnostics().reportWarn(index, WARN_EMPTY_PRAGMA);
                    continue;
                }

                // parse the pragma
                try {
                    PRAGMA_DISPATCH.execute(line.substring(1), parseFeatureSet);
                } catch (CommandSyntaxException e) {
                    context.diagnostics().reportErr(index, ERR_FAILED_PARSE_PRAGMA, e.getMessage());
                }


            } else if (!handleLineDiagnostics(context.diagnostics(), line, index)) {
                break;
            }
        }

        for (; index < lines.size(); index++) {
            final var line = lines.get(index).trim();

            if (handleLineDiagnostics(context.diagnostics(), line, index)) {
                continue;
            }

            if (line.startsWith("!")) { // Give diagnostic for improperly placed pragma.
                context.diagnostics().reportErr(index, ERR_BAD_PRAGMA);
            } else if (line.startsWith("$")) { // Handle macros
                if (line.equals("$")) {
                    context.diagnostics().reportWarn(index, WARN_EMPTY_MACRO);
                    continue;
                }

                // Macro pre-parsing
                final StringTemplate template;
                try {
                    template = StringTemplate.fromString(line.substring(1), index + 1);
                } catch (IllegalArgumentException e) {
                    // TODO: slightly better error handling here is required
                    context.diagnostics().reportErr(index, e.getMessage());
                    continue;
                }

                final var argIndex = new IntArrayList(
                    template.variables()
                        .stream()
                        .mapToInt(macroArgumentAllocator::getOrAllocateId)
                        .toArray()
                );

                codeEntries.add(MCFunctionLine.macro(index, template, argIndex));
            } else { // Handle everything else
                handleSingleLine(context, line, codeEntries, index);
            }
        }

        return new RawFunctionSource(codeEntries, macroArgumentAllocator, parseFeatureSet, ImmutableList.copyOf(lines));
    }

    public boolean hasMacro() {
        return !macroArgumentAllocator.entries().isEmpty();
    }

    public Optional<ExpandedFunctionSource> instantiate(CompileContext context,
                                                        Map<String, String> macroArgumentValues) {
        final var flatMacroVariables = new ArrayList<String>(macroArgumentValues.size());
        final var expandedCodeEntries = new ArrayList<MCFunctionLine>(codeEntries.size());
        var canContinue = true;


        for (final var key : macroArgumentAllocator.entries()) {
            if (!macroArgumentValues.containsKey(key)) {
                canContinue = false;
                context.diagnostics().reportErr(0, ERR_MISSING_MACRO, key);
            } else {
                flatMacroVariables.add(key);
            }
        }

        if (!canContinue) {
            return Optional.empty();
        }

        for (final var codeEntry : codeEntries) {
            switch (codeEntry.type()) {
            case MACRO -> {
                final var macro = codeEntry.macro();
                final var line = macro.instantiate(
                    macro.parameters().intStream().mapToObj(flatMacroVariables::get).toList()
                );

                handleSingleLine(context, line, expandedCodeEntries, codeEntry.lineNumber());
            }
            case PRE_PARSED, CONTROL_FLOW -> expandedCodeEntries.add(codeEntry);
            }
        }

        return Optional.of(new ExpandedFunctionSource(expandedCodeEntries, featureSet, rawSource));
    }

    public List<String> rawSource() {
        return rawSource;
    }
}
