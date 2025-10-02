package com.playmonumenta.papermixins.paperapi.v1.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class SweepingEdgeParticleEvent extends PlayerEvent implements Cancellable {
	private Location location;
	private boolean cancelled;

	public SweepingEdgeParticleEvent(Player player) {
		super(player);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
