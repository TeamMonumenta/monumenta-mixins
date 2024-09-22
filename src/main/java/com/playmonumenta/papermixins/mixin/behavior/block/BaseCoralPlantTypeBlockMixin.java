package com.playmonumenta.papermixins.mixin.behavior.block;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 * <p>
 * Di2sable coral death.
 */
@Mixin(BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {
	/**
	 * @author Flowey
	 * @reason Disable coral death.
	 */
	@Inject(
		method = "scanForWater",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void scanForWater(BlockState state, BlockGetter world, BlockPos pos,
									CallbackInfoReturnable<Boolean> cir) {
		if (MonumentaMod.getConfig().behavior.disableCoralDeath) {
			cir.setReturnValue(true);
		}
	}
}
