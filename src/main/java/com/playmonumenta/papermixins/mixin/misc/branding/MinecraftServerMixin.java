package com.playmonumenta.papermixins.mixin.misc.branding;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.VersionInfo;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
	@ModifyExpressionValue(
		method = "getServerModName",
		at = @At(
			value = "INVOKE",
			target = "Lio/papermc/paper/ServerBuildInfo;brandName()Ljava/lang/String;"
		)
	)
	private String modifyGetServerModName(String old) {
		return VersionInfo.IDENTIFIER;
	}
}
