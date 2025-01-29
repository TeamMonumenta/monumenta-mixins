package com.playmonumenta.papermixins.mixin.commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.playmonumenta.papermixins.MixinState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExecuteCommand.class)
public class ExecuteCommandMixin {
	@WrapOperation(
		method = "register",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/brigadier/CommandDispatcher;register" +
				"(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;",
			ordinal = 1
		)
	)
	private static LiteralCommandNode<CommandSourceStack> extractExecuteCommandNode(
		CommandDispatcher<CommandSourceStack> instance,
		LiteralArgumentBuilder<CommandSourceStack> command,
		Operation<LiteralCommandNode<CommandSourceStack>> original
	) {
		// remove the run node
		MixinState.EXECUTE_CHILDREN_COMMANDS = command
			.build()
			.getChildren()
			.stream()
			.filter(x -> !x.getName().equals("run"))
			.toList();

		return null;
	}
}
