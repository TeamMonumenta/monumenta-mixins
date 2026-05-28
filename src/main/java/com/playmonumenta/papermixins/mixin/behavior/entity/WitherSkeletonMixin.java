package com.playmonumenta.papermixins.mixin.behavior.entity;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherSkeleton.class)
public abstract class WitherSkeletonMixin extends AbstractSkeleton {
	protected WitherSkeletonMixin(EntityType<? extends AbstractSkeleton> type, Level level) {
		super(type, level);
	}

	@WrapOperation(
		method = "getArrow",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;igniteForSeconds(F)V"
		)
	)
	public void disableFlamingArrows(AbstractArrow instance, float v, Operation<Void> original) {
		if (!this.entityTags().contains("boss_no_flame_arrows")) {
			original.call(instance, v);
		}
	}

	@WrapOperation(
		method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;addEffect" +
				"(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;" +
				"Lorg/bukkit/event/entity/EntityPotionEffectEvent$Cause;)Z"
		)
	)
	public boolean disableWitherOnHit(LivingEntity instance, MobEffectInstance newEffect, Entity source,
									  EntityPotionEffectEvent.Cause cause, Operation<Boolean> original) {
		if (!this.entityTags().contains("boss_no_withering")) {
			original.call(instance, newEffect, source, cause);
		}

		return false;
	}
}
