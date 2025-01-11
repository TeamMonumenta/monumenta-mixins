package com.playmonumenta.papermixins.mixin.updatetmp;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.commands.TellRawCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TellRawCommand.class)
public class TellRawCommandMixin {
	@ModifyArg(
		method = "register",
		at = @At(
			ordinal = 1,
			value = "INVOKE",
			target = "Lnet/minecraft/commands/Commands;argument(Ljava/lang/String;" +
				"Lcom/mojang/brigadier/arguments/ArgumentType;)Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;"
		),
		index = 1
	)
	private static ArgumentType<?> replace(ArgumentType<?> type) {
		return StringArgumentType.greedyString();
	}
}
