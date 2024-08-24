package com.playmonumenta.papermixins.mixin.behavior.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
	@Overwrite
	public void afterDestroy(Level world, BlockPos pos, ItemStack tool) {
		if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) == 0) {
			world.removeBlock(pos, false);
		}
	}

	/**
	 * @author Flowey
	 * @reason Disable ice melting.
	 */
	@Overwrite
	public void melt(BlockState state, Level world, BlockPos pos) {
	}
}
