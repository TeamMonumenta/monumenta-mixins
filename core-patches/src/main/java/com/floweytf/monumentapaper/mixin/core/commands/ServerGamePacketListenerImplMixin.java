package com.floweytf.monumentapaper.mixin.core.commands;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Flowey
 * @mm-patch 0031-Monumenta-Patched-movement-exploit.patch
 * <p>
 * Mojank is bad at math.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Redirect(
        method = "handleMoveVehicle",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;)V"
        )
    )
    private void monumenta$warnOnTooManyPackets(Logger instance, String str) {
        instance.warn(str);
    }

    @Redirect(
        method = "handleMovePlayer",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        )
    )
    private void monumenta$warnOnTooManyPackets(Logger instance, String str, Object o1, Object o2) {
        instance.warn(str, o1, o2);
    }
}
