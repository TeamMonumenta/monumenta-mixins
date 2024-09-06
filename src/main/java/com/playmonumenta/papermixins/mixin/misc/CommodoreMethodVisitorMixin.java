package com.playmonumenta.papermixins.mixin.misc;

import org.objectweb.asm.MethodVisitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.bukkit.craftbukkit.v1_20_R3.util.Commodore$1$1")
public class CommodoreMethodVisitorMixin extends MethodVisitor {
    protected CommodoreMethodVisitorMixin(int api) {
        super(api);
    }

    @Inject(
        method = "visitMethodInsn",
        at = @At("HEAD"),
        cancellable = true
    )
    private void silenceServerLib(int opcode, String owner, String name, String desc, boolean itf, CallbackInfo ci) {
        // Serverlib might be shaded...
        // This is a very safe fork with no bugs whatsoever, so this check is stupid!
        if (owner.endsWith("ServerLib") && name.equals("checkUnsafeForks")) {
            // Nuke all serverlib calls...
            ci.cancel();
        }
    }
}
