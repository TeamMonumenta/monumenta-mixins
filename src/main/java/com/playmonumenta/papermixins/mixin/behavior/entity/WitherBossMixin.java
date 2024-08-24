package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Withers can be hurt by arrows even below half health.
 * Disable nether stars.
 */
@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Monster {
	protected WitherBossMixin(EntityType<? extends Monster> type, Level world) {
		super(type, world);
	}

	// It's never below half health (I promise!)
	@ModifyExpressionValue(
		method = "hurt",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;isPowered()Z"
		)
	)
	private boolean disableArrowInvulnerability(boolean original) {
		return false;
	}

	/**
	 * @author Flowey
	 * @reason Disable nether stars.
	 */
	@Overwrite
	public void dropCustomDeathLoot(@NotNull DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropCustomDeathLoot(source, lootingMultiplier, allowDrops);
	}
}
