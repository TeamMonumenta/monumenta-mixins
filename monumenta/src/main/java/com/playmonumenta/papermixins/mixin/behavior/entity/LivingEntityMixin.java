package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0016-Monumenta-Reset-last-player-hurt-time-on-taking-any-.patch
 * @mm-patch 0027-Monumenta-Handle-iframes-after-damage-event.patch
 * <p>
 * Read this code at your own risk. I hate this.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	@Nullable
	public Player lastHurtByPlayer;

	@Shadow
	public int lastHurtByPlayerTime;

	@Shadow
	public int invulnerableDuration;

	@Shadow
	public float lastHurt;

	@Shadow
	public int hurtDuration;

	@Shadow
	public int hurtTime;

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	// reset last hurt by player time regardless of dmg type
	@Inject(
		method = "hurt",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"
		)
	)
	private void resetHurtTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (lastHurtByPlayer != null) {
			lastHurtByPlayerTime = 100;
		}
	}

	@ModifyConstant(
		method = "hurt",
		constant = @Constant(
			intValue = 1,
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V"
			)
		)
	)
	private int setFlag(int constant) {
		return ((float) this.invulnerableTime > (float) this.invulnerableDuration / 2.0F) ? 0 : 1;
	}

	// forgive me
	// this is a huge mess...
	@ModifyExpressionValue(
		method = "hurt",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V"
			)
		)
	)
	private boolean disableIframeCheck(boolean original) {
		return true;
	}

	@Inject(
		method = "actuallyHurt",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/event/entity/EntityDamageEvent;getFinalDamage()D"
		),
		cancellable = true
	)
	private void performIframeCheck(
		DamageSource damagesource,
		float f,
		CallbackInfoReturnable<Boolean> cir,
		@Local EntityDamageEvent event
	) {
		// Monumenta: use post-event damage for iframes instead of pre-event damage
		if ((float) invulnerableTime > (float) invulnerableDuration / 2.0F) {
			float damage = (float) event.getDamage();
			if (damage <= lastHurt) {
				cir.setReturnValue(false);
				return;
			}
			this.lastHurt = damage;
		} else {
			this.lastHurt = (float) event.getDamage();
			this.invulnerableTime = this.invulnerableDuration;
			this.hurtDuration = 10;
			this.hurtTime = 10;
		}
	}

	// WARNING: BRAIN DAMAGE
	@WrapOperation(
		method = "hurt",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/LivingEntity;lastHurt:F"
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt" +
					"(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
				ordinal = 1
			)
		)
	)
	private void noop0(LivingEntity instance, float value, Operation<Void> original) {
	}

	@WrapOperation(
		method = "hurt",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I"
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt" +
					"(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
				ordinal = 1
			)
		)
	)
	private void noop1(LivingEntity instance, int value, Operation<Void> original) {
	}

	@WrapOperation(
		method = "hurt",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/LivingEntity;hurtDuration:I",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt" +
					"(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
				ordinal = 1
			)
		)
	)
	private void noop2(LivingEntity instance, int value, Operation<Void> original) {
	}

	@WrapOperation(
		method = "hurt",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I",
			ordinal = 0
		)
	)
	private void noop3(LivingEntity instance, int value, Operation<Void> original) {
	}
}
