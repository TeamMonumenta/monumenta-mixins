package com.playmonumenta.papermixins.mixin.misc.lifecycle;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(
		method = "stopServer",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/concurrent/ThreadPoolExecutor;shutdownNow()Ljava/util/List;",
			remap = false
		)
	)
	private void onStop(CallbackInfo ci) {
		MonumentaMod.onStop();
	}
}
