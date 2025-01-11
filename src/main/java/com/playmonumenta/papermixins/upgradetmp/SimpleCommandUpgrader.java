package com.playmonumenta.papermixins.upgradetmp;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import com.playmonumenta.papermixins.util.nbt.CompoundTagBuilder;
import com.playmonumenta.papermixins.util.nbt.ListTagBuilder;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// devtest tellraw @s "test"
public class SimpleCommandUpgrader {
	@FunctionalInterface
	public interface Handler {
		@Nullable
		String handle(CommandContextBuilder<CommandSourceStack> context, ParseResults<CommandSourceStack> results);
	}

	private static String extractText(StringRange range, ParseResults<?> results) {
		return results.getReader().getString().substring(
			range.getStart(),
			Mth.clamp(range.getEnd(), 0, results.getReader().getString().length())
		);
	}

	private static String extractText(ParsedCommandNode<?> node, ParseResults<?> results) {
		return extractText(node.getRange(), results);
	}

	private static String extractText(CommandContextBuilder<?> node, int i, ParseResults<?> results) {
		return extractText(node.getNodes().get(i), results);
	}

	private static String snipe(String old, StringRange range, String newText) {
		final var end = Mth.clamp(range.getEnd(), 0, old.length());
		return old.substring(0, range.getStart()) + newText + old.substring(end);
	}

	private static String snipe(String old, ParsedCommandNode<?> node, String newText) {
		return snipe(old, node.getRange(), newText);
	}

	private static String snipe(String old, CommandContextBuilder<?> node, int i, String newText) {
		return snipe(old, node.getNodes().get(i), newText);
	}

	private static String snipe(ParseResults<?> old, CommandContextBuilder<?> node, int i, String newText) {
		return snipe(old.getReader().getString(), node, i, newText);
	}

	private static Handler wrapSimple(Handler handler) {
		return (context, parseResult) -> {
			final var fixResult = handler.handle(context, parseResult);

			if (fixResult == null) {
				return parseResult.getReader().getString();
			}

			return snipe(parseResult.getReader().getString(), context.getRange(), fixResult);
		};
	}

	private static String mmSerializeSafe(Component component) {
		var text = MM.serialize(component);

		for (ChatFormatting value : ChatFormatting.values()) {
			text = text.replace("§" + value.getChar(), "<" + value.getName().replace("underline", "underlined") + ">");
		}

		text = text.replace("\n", "<newline>");
		return text;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger("SimpleCommandUpgraderMeow");
	private static final GsonComponentSerializer GSON_COMP_SER = GsonComponentSerializer.gson();
	private static final MiniMessage MM = MiniMessage.miniMessage();

	private static final List<Pair<String, Handler>> FIXERS =
		List.of(
			Pair.of("tellraw", wrapSimple((context, results) -> {
				// tellraw @a {"text":"test"}
				final var jsonText = extractText(context, 2, results);

				try {
					return "tellmini msg %s %s".formatted(
						extractText(context, 1, results),
						mmSerializeSafe(GSON_COMP_SER.deserialize(jsonText))
					);
				} catch (Exception e) {
					LOGGER.warn("illegal tellraw command (bad json component): `{}`", jsonText);
				}
				return null;
			})),
			Pair.of("title", wrapSimple((context, results) -> {
				// title @a title {"text":"test"}
				// title @a subtitle {"text":"test"}
				// title @a actionbar {"text":"test"}
				final var type = extractText(context, 2, results);
				if (!type.equals("title") && !type.equals("subtitle") && !type.equals("actionbar")) {
					return null;
				}
				final var jsonText = extractText(context, 3, results);
				try {
					return "tellmini %s %s %s".formatted(
						type,
						extractText(context, 1, results),
						mmSerializeSafe(GSON_COMP_SER.deserialize(jsonText))
					);
				} catch (Exception e) {
					LOGGER.warn("illegal title command (bad json component): `{}`", jsonText);
				}
				return null;
			})),
			Pair.of("give", (context, results) -> {
				if (!results.getReader().getString().contains("Text1")) {
					return results.getReader().getString();
				}

				try {
					final var arg = (ItemInput) context.getArguments().get("item").getResult();
					final var stack = arg.createItemStack(1, false);
					final var tag = stack.getOrCreateTag();
					var blockEntityTag = tag.getCompound("BlockEntityTag");

					List<String> entries = new ArrayList<>();

					for (int i = 1; i <= 4; i++) {
						if (blockEntityTag.contains("Text" + i)) {
							var str = blockEntityTag.getString("Text" + i);
							if (str.isEmpty()) {
								str = "\"\"";
							}
							entries.add(str);
							blockEntityTag.remove("Text" + i);
						} else {
							entries.add("\"\"");
						}
					}

					String color;

					if (blockEntityTag.contains("Color")) {
						color = blockEntityTag.getString("Color");
						blockEntityTag.remove("Color");
					} else {
						color = "black";
					}

					blockEntityTag.put("front_text", CompoundTagBuilder.of()
						.put("has_glowing_text", blockEntityTag.getBoolean("GlowingText"))
						.put("color", color)
						.put("messages", ListTagBuilder.of(entries.stream().map(StringTag::valueOf)))
						.get());

					return snipe(results, context, 2,
						BuiltInRegistries.ITEM.getKey(stack.getItem()).toString() + stack.getTag());
				} catch (Exception e) {
					LOGGER.warn("bwaa", e);
				}

				return results.getReader().getString();
			})
		);

	public static String updateSingle(String string) {
		if (string.startsWith("/")) {
			string = string.substring(1);
		}

		string = string.trim();

		final var dispatcher = MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher();
		final var command = MinecraftServer.getServer().createCommandSourceStack();

		final var parseResult = dispatcher.parse(string, command);

		if (parseResult.getReader().canRead()) {
			LOGGER.warn("failed to parse '%s'".formatted(string));
			return string;
		}

		var context = parseResult.getContext().getLastChild();

		for (final var fixer : FIXERS) {
			final var targetNode = dispatcher.getRoot().getChild(fixer.first());

			if (context.getNodes().isEmpty()) {
				continue;
			}

			if (context.getNodes().get(0).getNode() != targetNode) {
				continue;
			}

			return fixer.second().handle(context, parseResult);
		}

		return string;
	}
}
