package com.playmonumenta.papermixins.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.registry.commands.TellMiniCommand;
import net.minecraft.commands.CommandSourceStack;

public class CommandRegistry {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		TellMiniCommand.register(dispatcher);
	}
}
