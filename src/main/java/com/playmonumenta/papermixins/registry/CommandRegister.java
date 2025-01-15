package com.playmonumenta.papermixins.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.registry.commands.DebugCommands;
import com.playmonumenta.papermixins.registry.commands.TellMiniCommand;
import net.minecraft.commands.CommandSourceStack;

public class CommandRegister {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        TellMiniCommand.register(dispatcher);
        DebugCommands.register(dispatcher);
    }
}
