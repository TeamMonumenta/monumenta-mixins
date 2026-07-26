package com.playmonumenta.papermixins.mixin.api;

import com.playmonumenta.papermixins.impl.v1.MonumentaPaperAPIImpl;
import com.playmonumenta.papermixins.paperapi.v1.event.PacketEvent;
import io.papermc.paper.util.MCUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
	@Shadow
	public abstract ServerPlayer getPlayer();

	@ModifyVariable(
		method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V",
		at = @At("HEAD"),
		argsOnly = true
	)
	@SuppressWarnings("unchecked")
	public Packet<?> onSend(Packet<?> original) {
		if (MonumentaPaperAPIImpl.getInstance().stopNextPacketEvent) {
			return original;
		}
		@Nullable
		ServerPlayer player = getPlayer();
		if (player == null) {
			return original;
		}
		if (!MCUtil.isMainThread()) {
			return original;
		}
		if (original instanceof ClientboundBundlePacket bundlePacket) {
			Iterable<Packet<ClientGamePacketListener>> packets = bundlePacket.subPackets();
			ArrayList<Object> subPacketList = new ArrayList<>();
			for (Packet<ClientGamePacketListener> p : packets) {
				subPacketList.add(p);
			}
			ArrayList<Packet<ClientGamePacketListener>> newSubPacketList = new ArrayList<>(subPacketList.size());
			boolean modified = false;
			for (Packet<ClientGamePacketListener> originalPacket : packets) {
				PacketEvent event = new PacketEvent(player.getBukkitEntity(), originalPacket, subPacketList);
				event.callEvent();
				if (event.isCancelled()) {
					continue;
				}
				if (event.packetChanged() && event.getPacket() instanceof Packet<?> newPacket) {
					modified = true;
					// oh well, sorry type safety!
					newSubPacketList.add((Packet<ClientGamePacketListener>) newPacket);
					continue;
				}
				newSubPacketList.add(originalPacket);
			}
			if (modified) {
				return new ClientboundBundlePacket(newSubPacketList);
			}
		} else {
			PacketEvent event = new PacketEvent(player.getBukkitEntity(), original, null);
			event.callEvent();
			if (event.isCancelled()) {
				return original;
			}
			if (event.packetChanged() && event.getPacket() instanceof Packet<?> newPacket) {
				return newPacket;
			}
		}
		return original;
	}
}
