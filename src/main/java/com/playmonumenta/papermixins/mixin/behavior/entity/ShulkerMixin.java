package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0021-Monumenta-Fix-shulker-NoAI-allowing-peeking.patch
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Fix shulker peeking if NoAI is set.
 */
@Mixin(Shulker.class)
public abstract class ShulkerMixin extends AbstractGolem {
	protected ShulkerMixin(EntityType<? extends AbstractGolem> type, Level level) {
		super(type, level);
	}

	@Shadow protected abstract boolean isClosed();

	@ModifyExpressionValue(
		method = "hurtServer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/Shulker;isClosed()Z"
		)
	)
	private boolean allowArrowsOnClosed(boolean original) {
		return !ConfigManager.getConfig().behavior.closedShulkerHurtByArrows && isClosed();
	}
}
