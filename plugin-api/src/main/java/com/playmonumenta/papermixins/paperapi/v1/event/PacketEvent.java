package com.playmonumenta.papermixins.paperapi.v1.event;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

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

	/**
	 * Will only create events for packets initially in the bundle, added packets will also not be visible!
	 * events will remove/modify packets in 'bundle'
 	 */
	private final boolean isBundle;
	private final List<Object> packetsToAdd;

	public PacketEvent(Player player, Type type, Object packet, boolean isBundle) {
		super(player);
		this.type = type;
		this.packet = packet;
		this.isBundle = isBundle;
		this.packetsToAdd = new ArrayList<>();
	}

	public Type getType() {
		return type;
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
		return isBundle;
	}

	public void addBundlePackets(List<?> packets) {
		if (isBundle) {
			changed = true;
			packetsToAdd.addAll(packets);
		}
	}

	public List<Object> getPacketsToAdd() {
		return packetsToAdd;
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
