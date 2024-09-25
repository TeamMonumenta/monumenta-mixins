package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0021-Monumenta-Fix-shulker-NoAI-allowing-peeking.patch
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Fix shulker peeking if NoAI is set.
 */
@Mixin(Shulker.class)
public abstract class ShulkerMixin extends AbstractGolem {
	@Shadow protected abstract boolean isClosed();

	protected ShulkerMixin(EntityType<? extends AbstractGolem> type, Level world) {
		super(type, world);
	}

	@Inject(
		method = "setRawPeekAmount",
		at = @At("HEAD"),
		cancellable = true
	)
	private void cancelSetPeekAmountIfNoAI(int peekAmount, CallbackInfo ci) {
		if (MonumentaMod.getConfig().behavior.fixShulkerNoAi && isNoAi()) {
			ci.cancel();
		}
	}

	@ModifyExpressionValue(
		method = "hurt",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/Shulker;isClosed()Z"
		)
	)
	private boolean allowArrowsOnClosed(boolean original) {
		return MonumentaMod.getConfig().behavior.closedShulkerHurtByArrows ? false : isClosed();
	}
}
