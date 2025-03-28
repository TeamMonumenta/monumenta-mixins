package com.playmonumenta.papermixins.registry.commands;

import static com.playmonumenta.papermixins.util.CommandUtil.arg;
import static com.playmonumenta.papermixins.util.CommandUtil.lit;
import static com.playmonumenta.papermixins.util.CommandUtil.mcLitPred;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class TellMiniCommand {
	private static final MiniMessage MM = MiniMessage.builder()
		.editTags(tags -> {
			tags.resolvers(TagResolver.standard(),
			// unicode tag resolver - Flowey
				new TagResolver() {
					@Override
					public @NotNull Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments,
												@NotNull Context ctx) throws ParsingException {
						String value = arguments.popOr("Missing unicode value").value();
						String msg;

						try {
							msg = Character.toString(Integer.parseInt(value, 16));
						} catch (NumberFormatException e) {
							throw ctx.newException("Not a hex string", e, arguments);
						} catch (IllegalArgumentException e) {
							throw ctx.newException("Not a valid unicode codepoint", e, arguments);
						}

						return Tag.inserting(net.kyori.adventure.text.Component.text(msg));
					}

					@Override
					public boolean has(@NotNull String name) {
						return name.equals("uni") || name.equals("unicode");
					}
			});
		})
		.build();

	private static Command<CommandSourceStack> doTellMini(boolean hasArg, BiConsumer<ServerPlayer, Component> rec) {
		return c -> {
			final var recipients = EntityArgument.getPlayers(c, "recipients");
			final var message = hasArg
				? PaperAdventure.asVanilla(MM.deserialize(StringArgumentType.getString(c, "message")))
				: Component.empty();

			for (final var recipient : recipients) {
				rec.accept(recipient, ComponentUtils.updateForEntity(
					c.getSource(), message, recipient, 0
				));
			}

			return recipients.size();
		};
	}

	private static ArgumentBuilder<CommandSourceStack, ?> generateTellmini(BiConsumer<ServerPlayer, Component> rec) {
		return arg("recipients", EntityArgument.players(),
			doTellMini(false, rec),
			arg("message", StringArgumentType.greedyString(), doTellMini(true, rec))
		);
	}

	private static BiConsumer<ServerPlayer, Component> showTitle(Function<Component, Packet<?>> constructor) {
		return (serverPlayer, component) -> serverPlayer.connection.send(constructor.apply(component));
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(mcLitPred("tellmini",
			stack -> stack.hasPermission(2),
			lit("msg", generateTellmini(ServerPlayer::sendSystemMessage)),
			lit("title", generateTellmini(showTitle(ClientboundSetTitleTextPacket::new))),
			lit("subtitle", generateTellmini(showTitle(ClientboundSetSubtitleTextPacket::new))),
			lit("actionbar", generateTellmini(showTitle(ClientboundSetActionBarTextPacket::new))),
			generateTellmini(ServerPlayer::sendSystemMessage)
		));
	}
}
