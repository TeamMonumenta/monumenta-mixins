package com.playmonumenta.papermixins.mixin.misc;

import com.playmonumenta.papermixins.MonumentaMod;
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
    // TODO: should we also apply this change to aboveGroundVehicleTickCount? Current behaviour is yes, but
    //  restricting ordinal = 0 would fix
    @ModifyConstant(
        method = "tick",
        constant = @Constant(intValue = 80)
    )
    private int changeFlyingTickTime(int constant) {
        return MonumentaMod.getConfig().flyingTime;
    }
}
