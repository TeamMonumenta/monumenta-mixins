package com.playmonumenta.papermixins.mixin.commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0018-Monumenta-Ensure-minecraft-reload-uses-latest-Brigad.patch
 * <p>
 * Remove a bunch of CommandAPI errors.
 */
@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
	@Unique
	private static Commands monumenta$commandsInstance = null;

	@WrapOperation(
		method = "<init>",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/commands/Commands$CommandSelection;Lnet/minecraft/commands/CommandBuildContext;Z)Lnet/minecraft/commands/Commands;"
		)
	)
	private Commands cacheCommandInstance(Commands.CommandSelection commandSelection, CommandBuildContext context, boolean modern, Operation<Commands> original) {
		if (monumenta$commandsInstance == null) {
			monumenta$commandsInstance = original.call(commandSelection, context, modern);
		}

		return monumenta$commandsInstance;
	}
}
