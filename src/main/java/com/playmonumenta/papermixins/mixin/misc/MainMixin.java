package com.playmonumenta.papermixins.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.MonumentaMod;
import joptsimple.OptionSet;
import org.bukkit.craftbukkit.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {
	@Inject(
		method = "main",
		at = @At(
			value = "INVOKE",
			target = "Ljoptsimple/OptionSet;has(Ljava/lang/String;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Ljava/io/File;getAbsolutePath()Ljava/lang/String;",
				ordinal = 0
			)
		)
	)
	private static void handleNoPlugins(CallbackInfo ci, @Local OptionSet options) {
		if (options.has("no-plugins")) {
			MonumentaMod.HAS_PLUGINS = true;
		}
	}
}
