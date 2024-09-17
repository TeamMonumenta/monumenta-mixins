package com.playmonumenta.papermixins.mixin.misc;

import com.playmonumenta.papermixins.VersionInfo;
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
    private String modifyGetServerModName(String old) {
        return VersionInfo.IDENTIFIER;
    }
}
