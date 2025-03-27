package com.playmonumenta.papermixins.mixin.commands;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.Frame;
import net.minecraft.server.commands.ReturnCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReturnCommand.class)
public class ReturnCommandMixin {
	@ModifyExpressionValue(
		method = "register",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)" +
				"Lcom/mojang/brigadier/builder/ArgumentBuilder;"
		)
	)
	private static <T, S extends ArgumentBuilder<T, S>> ArgumentBuilder<T, S> addSimpleReturn(ArgumentBuilder<T, S> original) {
		original.executes((CustomCommandExecutor.CommandAdapter<T>) (source, contextChain, flags, control) -> {
			Frame frame = control.currentFrame();
			frame.discard();
		});

		return original;
	}
}
