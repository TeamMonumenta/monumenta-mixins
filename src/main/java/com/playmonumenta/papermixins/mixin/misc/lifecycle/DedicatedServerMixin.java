package com.playmonumenta.papermixins.mixin.misc.lifecycle;

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
	private void onStart(CallbackInfoReturnable<Boolean> cir) {
		MonumentaMod.onStart();
	}

	@Inject(
		method = "initServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/CraftServer;enablePlugins(Lorg/bukkit/plugin/PluginLoadOrder;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onPluginLoaded(CallbackInfoReturnable<Boolean> cir) {
		MonumentaMod.onPluginLoaded();
	}
}
