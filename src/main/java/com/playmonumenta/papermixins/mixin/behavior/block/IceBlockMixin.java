package com.playmonumenta.papermixins.mixin.behavior.block;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 * <p>
 * Disable ice behaviour.
 */
@Mixin(IceBlock.class)
public class IceBlockMixin {
	/**
	 * @author Flowey
	 * @reason Disable all special ice behaviour when harvested with non-silk touch tool.
	 */
	@Inject(
		method = "afterDestroy",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;dimensionType()" +
			"Lnet/minecraft/world/level/dimension/DimensionType;"),
		cancellable = true
	)
	public void afterDestroy(Level world, BlockPos pos, ItemStack tool, CallbackInfo ci) {
		if (MonumentaMod.getConfig().behavior.disableIceBreakBehavior) {
			world.removeBlock(pos, false);
			ci.cancel();
		}
	}

	/**
	 * @author Flowey
	 * @reason Disable ice melting.
	 */
	@Inject(
		method = "melt",
		at = @At("HEAD"),
		cancellable = true
	)
	public void melt(BlockState state, Level world, BlockPos pos, CallbackInfo ci) {
		if (MonumentaMod.getConfig().behavior.disableIceMelting) {
			ci.cancel();
		}
	}
}
