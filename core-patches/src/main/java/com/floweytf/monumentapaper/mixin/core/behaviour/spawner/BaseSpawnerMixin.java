package com.floweytf.monumentapaper.mixin.core.behaviour.spawner;

import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.floweytf.monumentapaper.duck.SpawnerAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * @author Flowey
 * @mm-patch 0007-Monumenta-Standardize-spawner-behaviour-for-all-enti.patch
 * @mm-patch 0025-Monumenta-Mobs-that-despawn-return-to-their-spawners.patch
 * <p>
 * Remove spawner checks.
 */
@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin implements SpawnerAccess {
    @Unique
    private BlockPos monumenta$blockPos = null;

    @ModifyExpressionValue(
        method = "serverTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/SpawnPlacements;checkSpawnRules" +
                "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/ServerLevelAccessor;" +
                "Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/core/BlockPos;" +
                "Lnet/minecraft/util/RandomSource;)Z"
        )
    )
    private boolean monumenta$disableMobSpawnCheck(boolean original, ServerLevel world, BlockPos pos) {
        // Since logic is inverted, we need to use !=
        // Also we should check for difficulty
        // TODO: there is probably a clever way of doing this without getting cancer
        return world.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL;
    }

    // TODO: validate this mixin actually selects the proper boolean value
    @ModifyConstant(
        method = "serverTick",
        constant = @Constant(
            intValue = 1
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lcom/destroystokyo/paper/event/entity/PreSpawnerSpawnEvent;callEvent()Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lcom/destroystokyo/paper/event/entity/PreSpawnerSpawnEvent;shouldAbortSpawn()Z"
            )
        )
    )
    private int monumenta$setFlagIfCancelled(
        int constant,
        @Local PreSpawnerSpawnEvent ev
    ) {
        return ev.shouldAbortSpawn() ? 1 : 0;
    }

    @ModifyExpressionValue(
        method = "serverTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z"
        )
    )
    private boolean monumenta$removeOrRightHandSide(boolean original) {
        return true;
    }

    @ModifyExpressionValue(
        method = "serverTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;" +
                "Lnet/minecraft/world/entity/MobSpawnType;)Z"
        )
    )
    private boolean monumenta$moveMobObstructionCheck(
        boolean original,
        ServerLevel world, BlockPos pos,
        @Local Mob mob
    ) {
        return world.isUnobstructed(mob);
    }

    @Override
    public BlockPos getBlockPos() {
        return monumenta$blockPos;
    }

    @Override
    public void setBlockPos(BlockPos pos) {
        monumenta$blockPos = pos;
    }
}
