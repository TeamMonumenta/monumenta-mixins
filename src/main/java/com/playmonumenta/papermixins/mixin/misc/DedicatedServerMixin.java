package com.playmonumenta.papermixins.mixin.misc;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(
        method = "initServer",
        at = @At(
            value = "INVOKE",
            target = "Lorg/spigotmc/SpigotConfig;init(Ljava/io/File;)V"
        )
    )
    private void logOurVersion(CallbackInfoReturnable<Boolean> cir) {
        MonumentaMod.LOGGER.info("Running {}", MonumentaMod.getIdentifier());
    }

    @Inject(
        method = "initServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/dedicated/DedicatedServerSettings;getProperties()" +
                "Lnet/minecraft/server/dedicated/DedicatedServerProperties;",
            shift = At.Shift.BY
        )
    )
    private void loadMixinConfig(CallbackInfoReturnable<Boolean> cir) {
        MonumentaMod.loadConfig();
    }
}
