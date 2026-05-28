package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE;

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
	public EntityReference<Player> lastHurtByPlayer;

	@Shadow
	public int lastHurtByPlayerMemoryTime;

	@Shadow
	public abstract double getAttributeValue(Holder<Attribute> attribute);

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	// reset last hurt by player time regardless of dmg type
	@Inject(
		method = "hurtServer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"
		)
	)
	private void resetHurtTime(ServerLevel level, DamageSource source, float damage,
							   CallbackInfoReturnable<Boolean> cir) {
		if (lastHurtByPlayer != null) {
			lastHurtByPlayerMemoryTime = 100;
		}
	}

	@ModifyExpressionValue(
		method = "hurtServer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/server/level/ServerLevel;broadcastDamageEvent" +
					"(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"
			)
		)
	)
	private boolean knockbackResistanceCheck(boolean original) {
		if (!ConfigManager.getConfig().behavior.verticalKb) {
			return original;
		}

		if (getAttributeValue(KNOCKBACK_RESISTANCE) >= 1) {
			return true;
		}

		return original;
	}

	@ModifyArg(
		method = "knockback(DDDLnet/minecraft/world/entity/Entity;" +
			"Lio/papermc/paper/event/entity/EntityKnockbackEvent$Cause;)V",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/event/CraftEventFactory;callEntityKnockbackEvent" +
				"(Lorg/bukkit/craftbukkit/entity/CraftLivingEntity;Lnet/minecraft/world/entity/Entity;" +
				"Lnet/minecraft/world/entity/Entity;Lio/papermc/paper/event/entity/EntityKnockbackEvent$Cause;" +
				"DLnet/minecraft/world/phys/Vec3;)Lio/papermc/paper/event/entity/EntityKnockbackEvent;"
		),
		index = 4
	)
	private double doVerticalKnockback(
		double originalY,
		@Local(argsOnly = true, ordinal = 0) double strength,
		@Local(name = "deltaMovement") Vec3 deltaMovement
	) {
		if (!ConfigManager.getConfig().behavior.verticalKb) {
			return originalY;
		}

		if (this.onGround()) {
			final var resistance = getAttributeValue(KNOCKBACK_RESISTANCE);
			return Math.min(0.4D * Math.max(1 - resistance, 0), deltaMovement.y / 2.0D + strength);
		}
		return deltaMovement.y;
	}
}
