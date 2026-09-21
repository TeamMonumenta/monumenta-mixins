package com.playmonumenta.papermixins.paperapi.v1.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the server saves the map_#.dat data for a map
 * <p>
 *  Cannot be cancelled, but can be saved in an additional location
 */
public class MapSaveEvent extends ServerEvent {
	private static final HandlerList handlers = new HandlerList();

	@NotNull
	private final String id;
	@NotNull
	private final Object data;

	public MapSaveEvent(@NotNull String id, @NotNull Object data) {
		this.id = id;
		this.data = data;
	}

	/**
	 * The map ID in the format "map_#", not including the data folder or .dat extension.
	 *
	 * @return The map ID as a string
	 */
	public @NotNull String getId() {
		return id;
	}

	/**
	 * Get the NBTTagCompound map data that will be saved.
	 *
	 * @return NBTTagCompound map data
	 */
	@NotNull
	public Object getData() {
		return data;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	@NotNull
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
