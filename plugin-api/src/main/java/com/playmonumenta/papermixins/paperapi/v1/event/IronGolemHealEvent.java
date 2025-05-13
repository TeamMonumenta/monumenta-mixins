package com.playmonumenta.papermixins.paperapi.v1.event;

import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class IronGolemHealEvent extends EntityEvent implements Cancellable {

	private Player player;
	private boolean cancelled;


	public IronGolemHealEvent(@NotNull Player player, @NotNull IronGolem ironGolem) {
		super(ironGolem);
		this.player = player;
	}


	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
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
