package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Stray.class)
public class StrayMixin {
	@Redirect(method = "getArrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Arrow;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
	public void disableSlownessArrows(Arrow instance, MobEffectInstance effect) {
		if (!Util.<AbstractSkeleton>c(this).getTags().contains("boss_no_slowness_arrows")) {
			instance.addEffect(effect);
		}
	}
}
