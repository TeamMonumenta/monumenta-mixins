package com.floweytf.monumentapaper.mixin.core.misc.branding;

import com.floweytf.monumentapaper.Monumenta;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * @author Flowey
 * @mm-patch 0001-Monumenta-MUST-BE-FIRST-Paperweight-build-changes-ba.patch
 * <p>
 * Implement some monumenta specific paper branding changes
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyConstant(
        method = "getServerModName",
        constant = @Constant(stringValue = "Paper")
    )
    private String monumenta$modifyGetServerModName(String old) {
        return Monumenta.IDENTIFIER;
    }
}
