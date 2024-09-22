package com.playmonumenta.papermixins.mcfunction;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import net.minecraft.commands.CommandSourceStack;

public record CompileContext(Diagnostics diagnostics, CommandDispatcher<CommandSourceStack> dispatcher,
							CommandSourceStack dummy) {
}
