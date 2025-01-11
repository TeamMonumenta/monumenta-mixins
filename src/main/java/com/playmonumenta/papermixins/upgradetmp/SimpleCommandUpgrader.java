package com.playmonumenta.papermixins.upgradetmp;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import it.unimi.dsi.fastutil.Pair;
import java.nio.file.Path;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.util.Files;

// devtest tellraw @s "test"
public class SimpleCommandUpgrader {
    @FunctionalInterface
    public interface Handler {
        @Nullable
        String handle(CommandContextBuilder<CommandSourceStack> context, ParseResults<CommandSourceStack> results);
    }

    private static String extractText(ParsedCommandNode<?> node, ParseResults<?> results) {
        return results.getReader().getString().substring(
            node.getRange().getStart(),
            Mth.clamp(node.getRange().getEnd(), 0, results.getReader().getString().length())
        );
    }

    private static String extractText(CommandContextBuilder<?> node, int i, ParseResults<?> results) {
        return extractText(node.getNodes().get(i), results);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("SimpleCommandUpgraderMeow");
    private static final GsonComponentSerializer GSON_COMP_SER = GsonComponentSerializer.gson();
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static final List<Pair<String, Handler>> FIXERS =
        List.of(
            Pair.of("tellraw", (context, results) -> {
                final var jsonText = extractText(context, 2, results);

                try {
                    return "tellmini msg %s %s".formatted(
                        extractText(context, 1, results),
                        MM.serialize(GSON_COMP_SER.deserialize(jsonText))
                    );
                } catch (Exception e) {
                    LOGGER.warn("illegal tellraw command (bad json component): `{}`", jsonText);
                }
                return null;
            }),
            Pair.of("title", (context, results) -> {
                return null;
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
            throw new IllegalArgumentException("failed to parse '%s'".formatted(string));
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

            final var fixResult = fixer.second().handle(context, parseResult);

            if (fixResult == null) {
                return string;
            }

            final var commandStr = parseResult.getReader().getString();
            final var begin = context.getRange().getStart();
            final var end = Mth.clamp(context.getRange().getEnd(), 0, commandStr.length());

            return commandStr.substring(0, begin) + fixResult + commandStr.substring(end);
        }

        return string;
    }
}
