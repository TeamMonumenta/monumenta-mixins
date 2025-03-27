package com.playmonumenta.papermixins.mcfunction.parse.parser;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.playmonumenta.papermixins.mcfunction.execution.FunctionExecSource;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.BlockAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1.LoopAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1.RunAST;
import com.playmonumenta.papermixins.util.CommandUtil;
import com.playmonumenta.papermixins.util.ExecuteCommandUtils;
import java.util.function.BiFunction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.UnboundEntryAction;

public class ExtensionsControlFlowV1Parser {
	public static final String CLOSING_TOKEN = "}";

	private static final CommandDispatcher<CommandSourceStack> DISPATCH = new CommandDispatcher<>();

	private static ASTNode parse(
		Parser parser, String text, int lineNo, String name, boolean isSubroutine,
		BiFunction<BlockAST, UnboundEntryAction<CommandSourceStack>, ASTNode> constructor
	) {
		parser.next();

		final var action = CommandUtil.parseCommand(
			DISPATCH,
			parser.dummy(),
			new StringReader(text),
			msg -> parser.diagnostics().reportErr(lineNo, "failed to parse '%s' statement: %s", name, msg)
		).orElse(null);

		return constructor.apply(
			new BlockAST(
				parser.parseBlock(
					() -> parser.parseNextCommand(false, isSubroutine), lineNo, CLOSING_TOKEN,
					"unclosed statement (missing '" + CLOSING_TOKEN + "')"
				)
			),
			action
		);
	}

	public static void init(CommandBuildContext access) {
		ExecuteCommandUtils.registerV1ControlFlow(DISPATCH, access, "run", context -> {
			final var source = (FunctionExecSource) context.getSource();
			source.getExecState().stack.peekSourceList().add(source);
			return 0;
		});

		ExecuteCommandUtils.registerV1ControlFlow(DISPATCH, access, "loop", context -> {
			final var source = (FunctionExecSource) context.getSource();
			source.getExecState().stack.peekSourceList().add(source);
			return 0;
		});

		Parser.register(
			"run",
			true,
			(p, text, lineNo, isTopLevel, isSubroutine) -> FeatureParseResult.ast(parse(p, text, lineNo, "run",
				isSubroutine, RunAST::new)),
			features -> !features.isV2ControlFlow(),
			"'run' is disabled when control-flow-v2 is enabled (consider adding 'pragma disable cfv2')"
		);

		Parser.register(
			"loop",
			true,
			(p, text, lineNo, isTopLevel, isSubroutine) -> FeatureParseResult.ast(parse(p, text, lineNo, "loop",
				isSubroutine, LoopAST::new)),
			features -> !features.isV2ControlFlow(),
			"'loop' is disabled when control-flow-v2' is enabled (consider adding 'pragma disable cfv2')"
		);

		// NOTE: it is *very* important that this is here, since we need to register *all* control-flow related actions
		// so that RawFunctionSource understands that it is a control flow statement rather than a regular command.
		// better error handling
		Parser.register(CLOSING_TOKEN, false, (p, text, lineNo, isTopLevel, isSubroutine) -> {
			p.diagnostics().reportErr(lineNo, "extraneous '" + CLOSING_TOKEN + "' (consider removing it)");
			p.next(); // eat it
			return FeatureParseResult.parseNext();
		});
	}
}
