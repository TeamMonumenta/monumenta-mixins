package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Giant;
import org.spigotmc.TrackingRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Increase viewing dist of giants.
 */
@Mixin(TrackingRange.class)
public class TrackingRangeMixin {
	@ModifyReturnValue(
		method = "getEntityTrackingRange",
		at = @At("TAIL")
	)
	private static int increaseGiantTrackingRange(int original, Entity entity) {
		if (entity instanceof Giant) {
			return ConfigManager.getConfig().behavior.giantTrackingRange;
		}

		return original;
	}
}
