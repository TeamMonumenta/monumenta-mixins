package com.playmonumenta.papermixins.mixin.api;

import com.playmonumenta.papermixins.paperapi.v1.event.PacketEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(method = "lambda$handleSignUpdate$19", at = @At("HEAD"), cancellable = true)
	@SuppressWarnings("unchecked")
	public void meow(ServerboundSignUpdatePacket packet, List<String> list1, CallbackInfo ci) {
		ServerPlayer player = this.player;
		PacketEvent event = new PacketEvent(player.getBukkitEntity(), PacketEvent.Type.INBOUND, packet, false);
		event.callEvent();
		if (event.isCancelled()) {
			ci.cancel();
			return;
		}
		if (event.packetChanged() && event.getPacket() instanceof Packet<?> newPacket) {
			((Packet<ServerGamePacketListener>) newPacket).handle((ServerGamePacketListener) this);
		}
	}
}
