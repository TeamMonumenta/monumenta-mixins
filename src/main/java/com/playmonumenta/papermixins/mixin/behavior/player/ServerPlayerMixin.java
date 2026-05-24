package com.playmonumenta.papermixins.mixin.behavior.player;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0005-Monumenta-Removed-test-for-monsters-when-sleeping-in.patch
 * @mm-patch 0015-Monumenta-Move-spawnpoint-set-for-sleeping-in-bed-af.patch
 * <p>
 * Player sleeping patches.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	public ServerPlayerMixin(Level level, GameProfile gameProfile) {
		super(level, gameProfile);
	}

	@Shadow
	public abstract boolean setRespawnPosition(ServerPlayer.@Nullable RespawnConfig respawnConfig, boolean showMessage
		, PlayerSetSpawnEvent.Cause cause);

	// TODO: figure out what this is trying to do
	@Inject(
		method = "getBedResult",
		at = @At(
			target = "Lnet/minecraft/world/attribute/BedRule;canSetSpawn(Lnet/minecraft/world/level/Level;)Z",
			value = "INVOKE"
		),
		cancellable = true
	)
	private void alwaysAllowSleeping(BlockPos pos, Direction direction,
									 CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
		cir.setReturnValue(Either.right(Unit.INSTANCE));
	}

	// Actually set spawnpoints now...
	@Inject(
		method = "startSleepInBed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Player;startSleepInBed(Lnet/minecraft/core/BlockPos;Z)" +
				"Lcom/mojang/datafixers/util/Either;"
		)
	)
	private void setSpawn(BlockPos pos, boolean force, CallbackInfoReturnable<Either<BedSleepingProblem, Unit>> cir) {
		this.setRespawnPosition(
			new ServerPlayer.RespawnConfig(
				LevelData.RespawnData.of(this.level().dimension(), pos, this.getYRot(), this.getXRot()),
				false
			), true, PlayerSetSpawnEvent.Cause.BED
		);
	}
}
