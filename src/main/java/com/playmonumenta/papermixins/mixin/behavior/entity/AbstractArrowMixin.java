package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0030-Monumenta-Disable-Bouncing-Arrows.patch
 */
@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {
	public AbstractArrowMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract byte getPierceLevel();

	@Inject(
		method = "onHitEntity",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;setRemainingFireTicks(I)V"
		),
		cancellable = true
	)
	private void disableBounce(EntityHitResult entityHitResult, CallbackInfo ci) {
		if(!MonumentaMod.getConfig().behavior.disableArrowBouncing) {
			return;
		}

		if (getPierceLevel() <= 10) {
			discard();
		}

		ci.cancel();
	}
}
