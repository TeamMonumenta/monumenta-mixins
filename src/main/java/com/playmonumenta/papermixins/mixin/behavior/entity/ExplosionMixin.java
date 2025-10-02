package com.playmonumenta.papermixins.mixin.behavior.entity;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * @author ashphyx
 * Force Knockback Resistance to work on explosions
 */
@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@ModifyExpressionValue(
		method = "explode",
		at = @At(
			value = "NEW",
			target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion(Lnet/minecraft/world/level/Explosion;)Z"
			)
		)
	)
	private Vec3 modifyResultValue(Vec3 original, @Local Entity entity) {
		if (ConfigManager.getConfig().behavior.explosionKbr && entity instanceof LivingEntity livingEntity) {
			return original.scale(Math.max(1 - (livingEntity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0));
		} else {
			return original;
		}
	}
}
