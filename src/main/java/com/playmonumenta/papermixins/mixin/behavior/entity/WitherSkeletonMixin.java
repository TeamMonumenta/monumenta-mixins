package com.playmonumenta.papermixins.mixin.behavior.entity;


import com.playmonumenta.papermixins.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherSkeleton.class)
public class WitherSkeletonMixin {
	@Redirect(method = "getArrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setSecondsOnFire(I)V"))
	public void disableFlamingArrows(AbstractArrow instance, int i) {
		if (!Util.<AbstractSkeleton>c(this).getTags().contains("boss_no_flame_arrows")) {
			instance.setSecondsOnFire(i);
		}
	}

	@Redirect(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;Lorg/bukkit/event/entity/EntityPotionEffectEvent$Cause;)Z"))
	public boolean disableWitherOnHit(LivingEntity instance, MobEffectInstance mobeffect, Entity entity, EntityPotionEffectEvent.Cause cause) {
		if (!Util.<AbstractSkeleton>c(this).getTags().contains("boss_no_withering")) {
			return instance.addEffect(mobeffect, entity, cause);
		}
		return false;
	}
}
