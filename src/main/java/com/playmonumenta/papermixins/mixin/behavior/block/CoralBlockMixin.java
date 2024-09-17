package com.playmonumenta.papermixins.mixin.behavior.block;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CoralBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    @Inject(
        method = "scanForWater",
        at = @At("HEAD"),
        cancellable = true
    )
    private void scanForWater(BlockGetter world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (MonumentaMod.getConfig().behavior.disableCoralDeath) {
            cir.setReturnValue(true);
        }
    }
}
