package com.playmonumenta.papermixins.mixin.behavior.spawner;

import com.mojang.authlib.GameProfile;
import com.playmonumenta.papermixins.duck.EntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0025-Monumenta-Mobs-that-despawn-return-to-their-spawners.patch
 * <p>
 * Mobs that despawn return to their spawners.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(
		method = "die",
		at = @At("TAIL")
	)
	private void onPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
		var center = new Location(level().getWorld(), this.getX(), this.getY(), this.getZ());
		var nearbyEntities = level().getWorld().getNearbyEntities(center, 24.0d, 24.0d, 24.0d);

		for (var nearby : nearbyEntities) {
			var accessor = (EntityAccess) ((CraftEntity) nearby).getHandle();
			accessor.monumenta$setSpawner(null);

			if (this.getTags().contains("DelvesPlayer")) {
				accessor.monumenta$setDelveReprime(true);
			}
		}
	}
}
