package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
	public ProjectileMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@ModifyExpressionValue(
		method = "canHitEntity",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;canBeHitByProjectile()Z"
		)
	)
	public boolean ignoreProjectilePassthrough(boolean original, @Local(argsOnly = true) Entity entity) {
		return original && !entity.getTags().contains("projectile_passthrough");
	}
}
