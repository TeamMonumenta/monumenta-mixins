package com.playmonumenta.papermixins.mixin.behavior.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.core.BlockPos;
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
	 * @reason Disable all special ice behavior when harvested with non-silk touch tool.
	 */
	@ModifyExpressionValue(
		method = "afterDestroy",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue" +
			"(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;")
	)
	public Object afterDestroy(Object original) {
		if (ConfigManager.getConfig().behavior.disableIceBreakBehavior) {
			return true;
		}

		return original;
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
	public void melt(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		if (ConfigManager.getConfig().behavior.disableIceMelting) {
			ci.cancel();
		}
	}
}
