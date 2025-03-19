package com.playmonumenta.papermixins.mixin.misc.debug;

import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.registry.commands.MixinDebugCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {
	@Shadow
	@Final
	protected ChunkPos chunkPos;

	@Inject(method = "setUnsaved", at = @At("HEAD"))
	private void setUnsavedLog(boolean needsSaving, CallbackInfo ci) {
		if (!needsSaving && MixinDebugCommand.STATE.get()) {
			final var e = new Exception();
			final var th = Thread.currentThread();
			MinecraftServer.getServer().submit(() -> {
				MinecraftServer.getServer().getPlayerList().broadcastSystemMessage(
					Component.literal("Chunk marked as unsaved @ [%s, %s]".formatted(chunkPos.x, chunkPos.z)),
					false
				);

				MonumentaMod.LOGGER.warn("meow meow! {} ", th.getName(), e);
			});
		}
	}
}
