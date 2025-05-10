package com.playmonumenta.papermixins.mixin.behavior.spawner;

import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.duck.EntityAccess;
import com.playmonumenta.papermixins.duck.SpawnerAccess;
import com.playmonumenta.papermixins.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
	private boolean disableMobSpawnCheck(boolean original, ServerLevel world, BlockPos pos) {
		// Since logic is inverted, we need to use !=
		// Also we should check for difficulty
		// TODO: there is probably a clever way of doing this without getting cancer
		// TODO: look at @Expression from MixinExtras (currently beta)
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
	private int setFlagIfCancelled(int constant, @Local PreSpawnerSpawnEvent ev, @Local boolean flag) {
		if(ev.shouldAbortSpawn()) {
			return 1; // true
		}

		return flag ? 1 : 0;
	}

	@ModifyExpressionValue(
		method = "serverTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z"
		)
	)
	private boolean removeOrRightHandSide(boolean original) {
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
	private boolean moveMobObstructionCheck(
		boolean original,
		ServerLevel world, BlockPos pos,
		@Local Mob mob
	) {
		return world.isUnobstructed(mob);
	}

	@Override
	public BlockPos monumenta$getBlockPos() {
		return monumenta$blockPos;
	}

	@Override
	public void monumenta$setBlockPos(BlockPos pos) {
		monumenta$blockPos = pos;
	}

	@Inject(
		method = "serverTick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/Entity;spawnedViaMobSpawner:Z"
		)
	)
	private void setEntitySpawnedBySpawner(ServerLevel world, BlockPos pos, CallbackInfo ci, @Local Entity entity) {
		if (!(entity instanceof FlyingMob || entity instanceof Vex)) {
			((EntityAccess) entity).monumenta$setSpawner(Util.c(this));
		}
	}
}
