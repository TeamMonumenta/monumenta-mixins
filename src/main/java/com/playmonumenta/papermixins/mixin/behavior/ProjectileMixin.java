package com.playmonumenta.papermixins.mixin.behavior;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
	@Shadow
	@Nullable
	public abstract Entity getOwner();

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
	public boolean canHitEntity(boolean original, @Local(argsOnly = true) Entity entity) {
		@Nullable
		Entity owner = getOwner();
		boolean playerHittingPlayer = owner != null && owner.getType() == EntityType.PLAYER && entity.getType() == EntityType.PLAYER;
		return original
			&& !(ConfigManager.getConfig().behavior.playerArrowsPassThroughPlayers && playerHittingPlayer)
			&& !entity.getTags().contains("projectile_passthrough");
	}
}
