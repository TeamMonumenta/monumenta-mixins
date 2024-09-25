package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * "don't override for grass/light, use super value"
 */
@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
	protected AnimalMixin(EntityType<? extends AgeableMob> type, Level world) {
		super(type, world);
	}

	/**
	 * @author Flowey
	 * @reason don't override for grass/light, use super value.
	 */
	@Inject(
		method = "getWalkTargetValue",
		at = @At("HEAD"),
		cancellable = true
	)
	public void getWalkTargetValue(BlockPos pos, LevelReader world, CallbackInfoReturnable<Float> cir) {
		if(MonumentaMod.getConfig().behavior.disableAnimalPathfindingWeights) {
			cir.setReturnValue(super.getWalkTargetValue(pos, world));
		}
	}
}
