package com.playmonumenta.papermixins.mixin.api;

import com.playmonumenta.papermixins.MixinState;
import com.playmonumenta.papermixins.paperapi.v1.event.PacketEvent;
import io.papermc.paper.util.MCUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
	@Shadow
	public abstract ServerPlayer getPlayer();

	@Unique
	@Nullable
	private static Packet<?> replacePacket = null;

	@Inject(
		method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V",
		at = @At("HEAD"),
		cancellable = true
	)
	@SuppressWarnings("unchecked")
	public void onSend(Packet<?> packet, PacketSendListener callbacks, boolean flush, CallbackInfo ci) {
		if (MixinState.stopNextOutboundPacketEvent) {
			MixinState.stopNextOutboundPacketEvent = false;
			return;
		}
		@Nullable
		ServerPlayer player = getPlayer();
		if (player == null) {
			return;
		}
		// Ignore handshake packets because why would we mess with that
		if (!MCUtil.isMainThread()) {
			return;
		}
		if (packet instanceof ClientboundBundlePacket bundlePacket) {
			List<Packet<ClientGamePacketListener>> packets = new ArrayList<>();
			for (Packet<ClientGamePacketListener> p : bundlePacket.subPackets()) {
				packets.add(p);
			}
			List<Packet<ClientGamePacketListener>> original = List.copyOf(packets);
			List<Packet<ClientGamePacketListener>> toAdd = new ArrayList<>();
			boolean modified = false;
			for (var iterator = packets.listIterator(); iterator.hasNext(); ) {
				var subPacket = iterator.next();
				if (!original.contains(subPacket)) {
					// don't fire an event for this one as it was removed by a previous event
					continue;
				}
				PacketEvent event = new PacketEvent(player.getBukkitEntity(), PacketEvent.Type.OUTBOUND, subPacket, true);
				event.callEvent();
				if (event.packetChanged()) {
					modified = true;
					iterator.set((Packet<ClientGamePacketListener>) event.getPacket());
				}
				if (event.isCancelled()) {
					iterator.remove();
				}
				for (Object p : event.getPacketsToAdd()) {
					toAdd.add((Packet<ClientGamePacketListener>) p);
				}
			}
			packets.addAll(toAdd);

			if (modified) {
				replacePacket = new ClientboundBundlePacket(packets);
			}
		} else {
			PacketEvent event = new PacketEvent(player.getBukkitEntity(), PacketEvent.Type.OUTBOUND, packet, false);
			event.callEvent();
			if (event.isCancelled()) {
				ci.cancel();
				return;
			}
			if (event.packetChanged() && event.getPacket() instanceof Packet<?> newPacket) {
				replacePacket = newPacket;
			}
		}
	}

	@ModifyVariable(
		method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;isConnected()Z"),
		argsOnly = true
	)
	// Modifies variable right after HEAD
	private Packet<?> modifyPacket(Packet<?> original) {
		if (replacePacket != null) {
			Packet<?> returnedPacket = replacePacket;
			replacePacket = null;
			return returnedPacket;
		}
		return original;
	}
}
