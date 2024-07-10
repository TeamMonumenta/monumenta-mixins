package com.floweytf.monumentapaper.mixin.core.behaviour.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 * <p>
 * Disable coral death.
 */
@Mixin(BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {
    /**
     * @author Flowey
     * @reason Disable coral death.
     */
    @Overwrite
    protected static boolean scanForWater(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }
}
