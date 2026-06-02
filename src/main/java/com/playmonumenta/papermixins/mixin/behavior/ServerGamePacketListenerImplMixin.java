package com.playmonumenta.papermixins.mixin.behavior;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Flowey
 * @mm-patch 0008-Monumenta-Increase-kicked-for-flying-timer-from-4s-t.patch
 * <p>
 * Make flying time configurable
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	// TODO: should we also apply this change to aboveGroundVehicleTickCount? Current behaviour is yes, but
	//  restricting ordinal = 0 would fix
	@ModifyConstant(
		method = "tick",
		constant = @Constant(intValue = 80)
	)
	private int changeFlyingTickTime(int constant) {
		return ConfigManager.getConfig().flyingTime;
	}
}
