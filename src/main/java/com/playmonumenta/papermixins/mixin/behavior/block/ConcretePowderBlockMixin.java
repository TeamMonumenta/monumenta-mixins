package com.playmonumenta.papermixins.mixin.behavior.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 * <p>
 * Disable concrete hardening when exposed to water in game.
 */
@Mixin(ConcretePowderBlock.class)
public class ConcretePowderBlockMixin {
	/**
	 * @author Flowey
	 * @reason Remove concrete hardening.
	 */
	@Overwrite

	private static boolean shouldSolidify(BlockGetter world, BlockPos pos, BlockState state) {
		return false;
	}

	/**
	 * @author Flowey
	 * @reason Remove concrete hardening.
	 */
	@Overwrite
	private static boolean touchesLiquid(BlockGetter world, BlockPos pos) {
		return false;
	}

	/**
	 * @author Flowey
	 * @reason Remove concrete hardening.
	 */
	@Overwrite
	private static boolean canSolidify(BlockState state) {
		return false;
	}
}
