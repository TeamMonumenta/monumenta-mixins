package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		return !ConfigManager.getConfig().behavior.disableWitherArrowInvuln && original;
	}

	/**
	 * @author Flowey
	 * @reason Disable nether stars.
	 */
	@Inject(
		method = "dropCustomDeathLoot",
		at = @At("HEAD"),
		cancellable = true
	)
	public void dropCustomDeathLoot(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci) {
		if(ConfigManager.getConfig().behavior.disableWitherStarDrop) {
			super.dropCustomDeathLoot(source, lootingMultiplier, allowDrops);
			ci.cancel();
		}
	}
}
