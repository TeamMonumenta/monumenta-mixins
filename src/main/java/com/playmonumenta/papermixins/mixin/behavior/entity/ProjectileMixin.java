package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.duck.SnowballAccess;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

	@Inject(
		method = "canHitEntity",
		at = @At("RETURN"),
		cancellable = true
	)
	private void skipPiercedSnowballEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			return;
		}

		if (!((Object) this instanceof Snowball snowball)) {
			return;
		}

		IntOpenHashSet piercedEntityIds = ((SnowballAccess) snowball).monumenta$getPiercedEntityIds();
		if (piercedEntityIds != null && piercedEntityIds.contains(entity.getId())) {
			cir.setReturnValue(false);
		}
	}
}
