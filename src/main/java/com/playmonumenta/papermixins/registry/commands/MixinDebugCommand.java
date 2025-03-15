package com.playmonumenta.papermixins.registry.commands;

import static com.playmonumenta.papermixins.util.CommandUtil.arg;
import static com.playmonumenta.papermixins.util.CommandUtil.lit;
import static com.playmonumenta.papermixins.util.CommandUtil.mcLitPred;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.playmonumenta.papermixins.debug.DebugMapGenerator;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;

public class MixinDebugCommand {
	public static final AtomicBoolean STATE = new AtomicBoolean(false);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(mcLitPred("monumenta-mixin-debug",
			stack -> stack.hasPermission(2),
			lit("givedebugmap", arg("players", EntityArgument.players(), context -> {
				EntityArgument.getPlayers(context, "players").forEach(serverPlayer -> {
					final var bk = serverPlayer.getBukkitEntity();
					bk.getInventory().addItem(DebugMapGenerator.createDebugMap(bk.getWorld()));
				});
				return 0;
			})),
			lit("dump_chunk_save", arg("value", BoolArgumentType.bool(), context -> {
				STATE.set(BoolArgumentType.getBool(context, "value"));
				return 0;
			}))
		));
	}
}
