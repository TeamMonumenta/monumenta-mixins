package com.floweytf.monumentapaper.mixin.core.behaviour.spawner;

import com.floweytf.monumentapaper.duck.SpawnerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0025-Monumenta-Mobs-that-despawn-return-to-their-spawners.patch
 * <p>
 * Mobs that despawn return to their spawners.
 */
@Mixin(SpawnerBlockEntity.class)
public class SpawnerBlockEntityMixin {
    @Shadow
    @Final
    private BaseSpawner spawner;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void monumenta$setBlockPosOnConstruction(BlockPos pos, BlockState state, CallbackInfo ci) {
        ((SpawnerAccess) this.spawner).setBlockPos(pos);
    }
}
