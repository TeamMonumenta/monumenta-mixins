package com.playmonumenta.papermixins.mixin.behavior.block;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	@Inject(
		method = "shouldSolidify",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void shouldSolidify(BlockGetter world, BlockPos pos, BlockState state,
									CallbackInfoReturnable<Boolean> cir) {
		if (ConfigManager.getConfig().behavior.disableConcreteHardening) {
			cir.setReturnValue(false);
		}
	}

	/**
	 * @author Flowey
	 * @reason Remove concrete hardening.
	 */
	@Inject(
		method = "touchesLiquid",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void touchesLiquid(BlockGetter world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (ConfigManager.getConfig().behavior.disableConcreteHardening) {
			cir.setReturnValue(false);
		}
	}

	/**
	 * @author Flowey
	 * @reason Remove concrete hardening.
	 */
	@Inject(
		method = "canSolidify",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void canSolidify(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (ConfigManager.getConfig().behavior.disableConcreteHardening) {
			cir.setReturnValue(false);
		}
	}
}
