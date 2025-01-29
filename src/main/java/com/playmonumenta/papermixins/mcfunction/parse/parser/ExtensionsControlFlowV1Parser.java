package com.playmonumenta.papermixins.mcfunction.parse.parser;

import static com.playmonumenta.papermixins.util.CommandUtil.arg;
import static com.playmonumenta.papermixins.util.CommandUtil.lit;
import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.playmonumenta.papermixins.MixinState;
import com.playmonumenta.papermixins.mcfunction.execution.CustomExecSource;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.BlockAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1.BreakAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1.LoopAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1.RunAST;
import com.playmonumenta.papermixins.util.CommandUtil;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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

	public static void registerV1ControlFlow(
		CommandDispatcher<CommandSourceStack> dispatcher, String name,
		Command<CommandSourceStack> command
	) {
		final var builder = literal(name).then(literal("{").executes(command));
		Objects.requireNonNull(MixinState.EXECUTE_CHILDREN_COMMANDS).forEach(builder::then);
		dispatcher.register(builder);
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		registerV1ControlFlow(DISPATCH, "run", context -> {
			final var source = (CustomExecSource<Consumer<CommandSourceStack>>) context.getSource();
			source.getState().accept(source);
			return 0;
		});

		registerV1ControlFlow(DISPATCH, "loop", context -> {
			final var source = (CustomExecSource<Consumer<CommandSourceStack>>) context.getSource();
			source.getState().accept(source);
			return 0;
		});

		DISPATCH.register(lit("break",
			context -> {
				throw new IllegalStateException();
			},
			arg("amount", IntegerArgumentType.integer(1), context -> {
				throw new IllegalStateException();
			})
		));

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

		Parser.register(
			"break",
			true,
			(parser, text, lineNo, isTopLevel, isInSubroutine) -> {
				parser.next();
				final var parts = text.split(" ");
				return FeatureParseResult.ast(new BreakAST(lineNo, parts.length == 1 ? 1 : Integer.parseInt(parts[1])));
			},
			features -> !features.isV2ControlFlow(),
			"'break' is disabled when control-flow-v2' is enabled (consider adding 'pragma disable cfv2')"
		);

		// better error handling
		Parser.register(CLOSING_TOKEN, false, (p, text, lineNo, isTopLevel, isSubroutine) -> {
			p.diagnostics().reportErr(lineNo, "extraneous '" + CLOSING_TOKEN + "' (consider removing it)");
			p.next(); // eat it
			return FeatureParseResult.parseNext();
		});
	}
}
