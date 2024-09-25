package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0024-Monumenta-Remove-bee-death-neutral-after-sting.patch
 * <p>
 * Bees should not lose agro on death
 */
@Mixin(Bee.class)
public class BeeMixin {
	@WrapOperation(method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/animal/Bee;setHasStung(Z)V"
		)
	)
	private void cancelSetHasStung(Bee instance, boolean hasStung, Operation<Void> original) {
		if(!MonumentaMod.getConfig().behavior.keepBeeAgroAfterSting) {
			original.call(instance, hasStung);
		}
	}

	@WrapOperation(method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/animal/Bee;stopBeingAngry()V"
		)
	)
	private void cancelStopBeingAngry(Bee instance, Operation<Void> original) {
		if(!MonumentaMod.getConfig().behavior.keepBeeAgroAfterSting) {
			original.call(instance);
		}
	}
}
