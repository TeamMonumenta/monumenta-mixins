package com.playmonumenta.papermixins.mcfunction.parse.parser;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ReturnAST;
import com.playmonumenta.papermixins.util.CommandUtil;
import com.playmonumenta.papermixins.util.ComponentUtils;

public class BaseParser {
	private static final CommandDispatcher<Parser> PRAGMA_DISPATCH = new CommandDispatcher<>();

	public static void init() {
		final var PRAGMA_BAD_FEAT = CommandUtil.exceptionType(s -> ComponentUtils.fLiteral("unknown pragma feature flag %s", s));

		PRAGMA_DISPATCH.register(CommandUtil.lit("pragma", CommandUtil.lit("enable", CommandUtil.arg("flag", StringArgumentType.word(), (context) -> {
			final var value = StringArgumentType.getString(context, "flag");
			if (context.getSource().features.enable(StringArgumentType.getString(context, "flag"))) {
				throw PRAGMA_BAD_FEAT.create(value);
			}
			return 0;
		})), CommandUtil.lit("disable", CommandUtil.arg("flag", StringArgumentType.word(), (context) -> {
			final var value = StringArgumentType.getString(context, "flag");
			if (context.getSource().features.disable(StringArgumentType.getString(context, "flag"))) {
				throw PRAGMA_BAD_FEAT.create(value);
			}
			return 0;
		}))));

		Parser.register(
			"pragma",
			false,
			(parser, text, lineNo, context) -> {
				try {
					PRAGMA_DISPATCH.execute(text, parser);
				} catch (CommandSyntaxException e) {
					parser.context.reportErr(lineNo, "failed to parse pragma: %s", e.getMessage());
				}

				parser.reader.next(); // eat it
				return FeatureParseResult.parseNext();
			}
		);

		Parser.register(
			"return",
			false,
			(parser, text, lineNo, context) -> {
				if (!text.equals("return")) {
					return FeatureParseResult.fallthrough();
				}

				parser.reader.next();
				return FeatureParseResult.ast(new ReturnAST());
			}
		);
	}
}
