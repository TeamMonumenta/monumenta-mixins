package com.floweytf.monumentapaper.duck;

import net.minecraft.core.BlockPos;

public interface SpawnerAccess {
    BlockPos getBlockPos();

    void setBlockPos(BlockPos pos);
}
