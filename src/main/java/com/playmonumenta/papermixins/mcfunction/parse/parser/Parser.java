package com.playmonumenta.papermixins.mcfunction.parse.parser;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.mcfunction.CompileContext;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ParseFeatureSet;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.BlockAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CommandAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.TopLevelAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.subroutine.SubroutineDefinitionAST;
import com.playmonumenta.papermixins.mcfunction.parse.reader.ExpandedFunctionSource;
import com.playmonumenta.papermixins.mcfunction.parse.reader.MCFunctionLine;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public class Parser {
    interface ParseHandler {
        FeatureParseResult doParse(
            Parser parser,
            String text,
            int lineNo,
            boolean isTopLevel,
            boolean isInSubroutine
        );
    }

    private record FeatureHandlerEntry(ParseHandler handler, boolean recvMacro, Predicate<ParseFeatureSet> enablePred,
                                       String notEnabledWarning) {
    }

    private static final Map<String, FeatureHandlerEntry> FEATURE_HANDLER = new HashMap<>();
    private final CompileContext context;
    private final ExpandedFunctionSource source;
    private int index = 0;

    public Parser(CompileContext context, ExpandedFunctionSource source) {
        this.context = context;
        this.source = source;
    }

    static void register(
        String name, boolean recvMacro, ParseHandler handler, Predicate<ParseFeatureSet> enablePred, String error
    ) {
        FEATURE_HANDLER.put(name, new FeatureHandlerEntry(handler, recvMacro, enablePred, error));
    }

    static void register(String name, boolean recvMacro, ParseHandler handler) {
        register(name, recvMacro, handler, f -> true, null);
    }

    public static void init(CommandBuildContext access) {
        ExtensionsControlFlowV1Parser.init(access);
        ExtensionSubroutineParser.init();
    }

    public static Set<String> controlFlowKeywords() {
        return FEATURE_HANDLER.keySet();
    }

    public boolean present() {
        return index < source.codeEntries().size();
    }

    public MCFunctionLine curr() {
        return source.codeEntries().get(index);
    }

    public void next() {
        index++;
    }

    public Diagnostics diagnostics() {
        return context.diagnostics();
    }

    public CommandDispatcher<CommandSourceStack> dispatcher() {
        return context.dispatcher();
    }

    public CommandSourceStack dummy() {
        return context.dummy();
    }

    public List<ASTNode> parseBlock(Supplier<ASTNode> parseOne, int startLineNo, String terminator,
                                    String onUnterminated) {
        int prevIndex = -1;

        final var body = new ArrayList<ASTNode>();

        while (present()) {
            // Rather than leak memory by infinite loop, we check if the parser has eaten the next token
            // This forces the parser to terminate in linear bounded time (context-sensitive grammar)
            if (index == prevIndex) {
                throw new IllegalStateException("internal error: parser failed to advance");
            }

            prevIndex = index;

            if (curr().type() == MCFunctionLine.Type.CONTROL_FLOW && curr().controlFlow().first().equals(terminator)) {
                next();
                return body;
            }

            final var ast = parseOne.get();


            if (ast != null) {
                body.add(ast);
            }
        }

        diagnostics().reportErr(startLineNo, onUnterminated);
        return body;
    }

    @Nullable
    public ASTNode parseNextCommand(boolean isTopLevel, boolean isInSubroutine) {
        var prevIndex = -1;

        while (present()) {
            // Rather than leak memory by infinite loop, we check if the parser has eaten the next token
            // This forces the parser to terminate in linear bounded time (context-sensitive grammar)
            if (index == prevIndex) {
                throw new IllegalStateException("internal error: parser failed to advance");
            }

            prevIndex = index;

            final var lineNo = curr().lineNumber();

            if (curr().type() == MCFunctionLine.Type.CONTROL_FLOW) {
                final var data = curr().controlFlow();
                final var entry = FEATURE_HANDLER.get(data.key());

                // Handle feature parsers
                // TODO: this branch should always be taken, but I'm not willing to try and confirm that.
                if (entry != null) {
                    if (!entry.enablePred.test(source.featureSet())) {
                        diagnostics().reportWarn(lineNo, entry.notEnabledWarning);
                    } else {
                        final var res = entry.handler.doParse(
                            this, data.second(), lineNo,
                            isTopLevel, isInSubroutine
                        );

                        switch (res.action()) {
                        case RETURN:
                            return res.value();
                        case CONTINUE:
                            continue;
                        case FALLTHROUGH:
                        }
                    }
                }
            }

            next();

            return new CommandAST(curr().preParsed());
        }

        return null;
    }

    public ASTNode parseTopLevel() {
        final var children = new ArrayList<ASTNode>();
        final var subroutine = new ArrayList<SubroutineDefinitionAST>();

        while (present()) {
            final var ast = parseNextCommand(true, false);
            if (ast == null)
                continue;

            if (ast instanceof SubroutineDefinitionAST subroutineDefinitionAST) {
                subroutine.add(subroutineDefinitionAST);
            } else {
                children.add(ast);
            }
        }

        return new TopLevelAST(new BlockAST(children), subroutine);
    }
}
