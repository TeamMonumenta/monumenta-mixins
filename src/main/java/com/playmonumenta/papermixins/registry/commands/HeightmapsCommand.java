package com.playmonumenta.papermixins.registry.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

import static com.playmonumenta.papermixins.util.CommandUtil.arg;
import static com.playmonumenta.papermixins.util.CommandUtil.lit;
import static com.playmonumenta.papermixins.util.CommandUtil.mcLitPred;

public class HeightmapsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(mcLitPred("monumenta-mixin-heightmaps",
            stack -> stack.hasPermission(2),
            lit("reset",
                arg("from", Vec2Argument.vec2(),
                    arg("to", Vec2Argument.vec2(), context -> {
                        final var from = Vec2Argument.getVec2(context, "from");
                        final var to = Vec2Argument.getVec2(context, "to");

                        final var fromChunk = new ChunkPos(new BlockPos((int) from.x, 0, (int) from.y));
                        final var toChunk = new ChunkPos(new BlockPos((int) to.x, 0, (int) to.y));

                        final var fromX = Math.min(fromChunk.x, toChunk.x);
                        final var toX = Math.max(fromChunk.x, toChunk.x);

                        final var fromZ = Math.min(fromChunk.z, toChunk.z);
                        final var toZ = Math.max(fromChunk.z, toChunk.z);

                        final var level = context.getSource().getLevel();

                        var touched = 0;
                        for (int chunkX = fromX; chunkX <= toX; chunkX++) {
                            for (int chunkZ = fromZ; chunkZ <= toZ; chunkZ++) {
                                final var chunk = level.getChunkIfLoaded(chunkX, chunkZ);

                                if (chunk == null) {
                                    continue;
                                }

                                chunk.heightmaps.clear();
                                touched++;
                            }
                        }

                        int finalTouched = touched;

                        context.getSource().sendSuccess(
                            () -> Component.literal("")
                                .append("Heightmaps reset for ")
                                .append(Component.literal(String.valueOf(finalTouched)).withStyle(ChatFormatting.GREEN))
                                .append(" chunk(s)."),
                            true
                        );

                        return 0;
                    })
                )
            )
        ));
    }
}
