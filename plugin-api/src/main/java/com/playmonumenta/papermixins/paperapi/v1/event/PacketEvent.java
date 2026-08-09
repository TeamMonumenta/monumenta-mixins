package com.playmonumenta.papermixins.paperapi.v1.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class PacketEvent extends PlayerEvent implements Cancellable {
	public enum Type {
		OUTBOUND,
		INBOUND
	}
	private final Type type;
	// throw this in version adapter, teehee!
	private Object packet;
	private boolean cancelled = false;
	private boolean changed = false;
	@Nullable
	private final List<Object> allBundledPackets;

	public PacketEvent(Player player, Type type, Object packet, @Nullable List<Object> allBundledPackets) {
		super(player);
		this.type = type;
		this.packet = packet;
		this.allBundledPackets = allBundledPackets;
	}

	public Object getPacket() {
		return packet;
	}

	public void setPacket(Object newPacket) {
		this.packet = newPacket;
		changed = true;
	}

	public boolean packetChanged() {
		return changed;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public boolean isBundlePacket() {
		return allBundledPackets != null;
	}

	public void modifyBundlePackets(Consumer<List<Object>> packetHandler) {
		if (allBundledPackets != null) {
			packetHandler.accept(allBundledPackets);
		}
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();

	@NotNull
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
