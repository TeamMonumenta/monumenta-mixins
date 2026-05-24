package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Charged creepers no longer cause mob head drops.
 */
@Mixin(Creeper.class)
public class CreeperMixin {
	/**
	 * @author Flowey
	 * @reason Disable skull drop.
	 */
	@ModifyExpressionValue(
		method = "killedEntity",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;isPowered()Z")
	)
	public boolean canDropMobsSkull(boolean original) {
		if(ConfigManager.getConfig().behavior.disableChargedCreeperHeads) {
			return false;
		}

		return original;
	}
}
