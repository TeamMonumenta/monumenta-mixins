package com.playmonumenta.papermixins.mixin.behavior.player;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
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
    public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    public abstract boolean setRespawnPosition(
        ResourceKey<Level> dimension,
        @Nullable BlockPos pos,
        float angle,
        boolean forced,
        boolean sendMessage,
        PlayerSetSpawnEvent.Cause cause
    );

    // Move spawnpoint set for sleeping in bed after event
    // Also, sleeping checks are disabled now...
    @Inject(
        method = "getBedResult",
        at = @At(
            target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition" +
                "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;" +
                "FZZLcom/destroystokyo/paper/event/player/PlayerSetSpawnEvent$Cause;)Z",
            value = "INVOKE"
        ),
        cancellable = true
    )
    private void alwaysAllowSleeping(BlockPos _0, Direction _1,
                                               CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        cir.setReturnValue(Either.right(Unit.INSTANCE));
        cir.cancel();
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
    private void setSpawn(BlockPos pos, boolean _0,
                                    CallbackInfoReturnable<Either<BedSleepingProblem, Unit>> cir) {
        this.setRespawnPosition(
            level().dimension(),
            pos,
            getYRot(),
            false,
            true,
            PlayerSetSpawnEvent.Cause.BED
        );
    }
}
