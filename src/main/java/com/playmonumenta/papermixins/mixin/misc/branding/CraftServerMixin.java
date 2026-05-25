package com.playmonumenta.papermixins.mixin.misc.branding;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.VersionInfo;
import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0001-Monumenta-MUST-BE-FIRST-Paperweight-build-changes-ba.patch
 * <p>
 * Implement some monumenta specific paper branding changes.
 */
@Mixin(CraftServer.class)
public class CraftServerMixin {
	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lio/papermc/paper/ServerBuildInfo;brandName()Ljava/lang/String;"
		)
	)
	private String modifyServerName(String string) {
		return VersionInfo.IDENTIFIER + string;
	}

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			target = "Lio/papermc/paper/ServerBuildInfo;asString(Lio/papermc/paper/ServerBuildInfo$StringRepresentation;)Ljava/lang/String;",
			value = "INVOKE"
		)
	)
	private String modifyImplementationVersion(String original) {
		if (original == null) {
			return VersionInfo.IDENTIFIER + "-Paper";
		}
		return original.replace("Paper", VersionInfo.IDENTIFIER + "-Paper");
	}
}
