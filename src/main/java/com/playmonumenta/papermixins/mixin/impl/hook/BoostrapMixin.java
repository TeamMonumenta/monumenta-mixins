package com.playmonumenta.papermixins.mixin.impl.hook;

import com.playmonumenta.papermixins.impl.v1.hook.HookRegistryImpl;
import net.minecraft.server.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public class BoostrapMixin {
    @Inject(
        method = "lambda$bootStrap$0",
        at = @At("HEAD")
    )
    private static void unfreezeHookRegistry(CallbackInfo ci) {
        HookRegistryImpl.isReady = true;
    }

    @Inject(
        method = "lambda$bootStrap$0",
        at = @At("TAIL")
    )
    private static void freezeHookRegistry(CallbackInfo ci) {
        HookRegistryImpl.isReady = false;
    }
}
