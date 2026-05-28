package com.playmonumenta.papermixins.mixin.behavior;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * @author Flowey
 * @mm-patch 0008-Monumenta-Increase-kicked-for-flying-timer-from-4s-t.patch
 * <p>
 * Make flying time configurable
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@ModifyConstant(
		method = "getMaximumFlyingTicks",
		constant = @Constant(doubleValue = 80.0)
	)
	private double changeFlyingTickTime(double constant) {
		return ConfigManager.getConfig().flyingTime;
	}
}
