package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Ban zombie drowning.
 */
@Mixin(Zombie.class)
public class ZombieMixin {
	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/Zombie;startUnderWaterConversion(I)V"
		)
	)
	private void disableConversion(Zombie instance, int ticksUntilWaterConversion, Operation<Void> original) {
		if (!MonumentaMod.getConfig().behavior.disableDrownConversion) {
			original.call(instance, ticksUntilWaterConversion);
		}
	}
}
