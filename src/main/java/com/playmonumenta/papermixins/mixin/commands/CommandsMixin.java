package com.playmonumenta.papermixins.mixin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.playmonumenta.papermixins.mcfunction.parse.parser.Parser;
import com.playmonumenta.papermixins.registry.CommandRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandsMixin {
	@Shadow
	@Final
	private CommandDispatcher<CommandSourceStack> dispatcher;

	@Inject(
		method = "<init>(Lnet/minecraft/commands/Commands$CommandSelection;" +
			"Lnet/minecraft/commands/CommandBuildContext;)V",
		at = @At("TAIL")
	)
	private void registerCommands(Commands.CommandSelection environment,
								CommandBuildContext commandRegistryAccess,
								CallbackInfo ci) {
		CommandRegistry.register(this.dispatcher);
		Parser.init(commandRegistryAccess);
	}
}
