package com.floweytf.monumentapaper.mixin.core.behaviour.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CoralBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 * <p>
 * Disable coral death.
 */
@Mixin(CoralBlock.class)
public class CoralBlockMixin {
    /**
     * @author Flowey
     * @reason Disable coral death.
     */
    @Overwrite
    protected boolean scanForWater(BlockGetter world, BlockPos pos) {
        return true;
    }
}
