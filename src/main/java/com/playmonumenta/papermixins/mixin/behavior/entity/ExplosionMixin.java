package com.playmonumenta.papermixins.mixin.behavior.entity;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author ashphyx
 * Force Knockback Resistance to work on explosions
 */
@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@ModifyExpressionValue(method = "explode",
			at = @At(value = "NEW",
					target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
					ordinal = 2)
	)
	private Vec3 result(Vec3 original, @Local Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			return original.scale(Math.max(1 - (livingEntity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0));
		} else {
			return original;
		}
	}
}
