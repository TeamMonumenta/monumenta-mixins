package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import org.bukkit.craftbukkit.CraftWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Patches {@link CraftWorld} chunk unloading
 * Version specific to 1.20.4, needs to be nuked
 */
@Mixin(CraftWorld.class)
public class CraftWorldMixin {
	@ModifyArg(
		method = "unloadChunk0",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;setUnsaved(Z)V")
	)
	private boolean fixSetUnsaved(boolean needsSaving) {
		return !needsSaving;
	}
}
