package com.playmonumenta.papermixins.duck;

import net.minecraft.core.BlockPos;

public interface SpawnerAccess {
    BlockPos monumenta$getBlockPos();

    void monumenta$setBlockPos(BlockPos pos);
}
