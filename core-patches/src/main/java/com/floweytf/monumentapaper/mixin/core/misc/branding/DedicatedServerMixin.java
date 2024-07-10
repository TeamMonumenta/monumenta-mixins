package com.floweytf.monumentapaper.mixin.core.misc.branding;

import com.floweytf.monumentapaper.Monumenta;
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
    private void monumenta$logOurVersion(CallbackInfoReturnable<Boolean> cir) {
        Monumenta.LOGGER.info("Running {}", Monumenta.getIdentifier());
    }
}
