package com.floweytf.monumentapaper.mixin.core.misc.branding;

import com.floweytf.monumentapaper.Monumenta;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * @author Flowey
 * @mm-patch 0001-Monumenta-MUST-BE-FIRST-Paperweight-build-changes-ba.patch
 * <p>
 * Implement some monumenta specific paper branding changes.
 */
@Mixin(CraftServer.class)
public class CraftServerMixin {
    @ModifyConstant(
        method = "<init>",
        constant = @Constant(stringValue = "Paper")
    )
    private String monumenta$modifyServerName(String string) {
        return Monumenta.IDENTIFIER + string;
    }

    @ModifyExpressionValue(
        method = "<init>",
        at = @At(
            target = "Ljava/lang/Package;getImplementationVersion()Ljava/lang/String;",
            value = "INVOKE"
        )
    )
    private String monumenta$modifyImplementationVersion(String original) {
        return original.replace("Paper", Monumenta.IDENTIFIER + "Paper");
    }
}
