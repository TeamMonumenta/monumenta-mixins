package com.playmonumenta.papermixins.paperapi.v1.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when the server loads the data for a map
 */
public class MapLoadEvent extends ServerEvent {
	private static final HandlerList handlers = new HandlerList();

	@NotNull
	private final String id;
	@Nullable
	private Object data;

	public MapLoadEvent(@NotNull String id) {
		super();
		this.id = id;
		this.data = null;
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
	 * Get the data supplied by an earlier call to {@link #setData}.
	 * <p>
	 * This data will be used instead of loading the map's file. It is null unless
	 * supplied by a plugin.
	 *
	 * @return NBTTagCompound data of the map's .dat file as set by {@link #setData}
	 */
	@Nullable
	public Object getData() {
		return data;
	}

	/**
	 * Set the data to use for the map's data instead of loading it from a file.
	 * <p>
	 * This data will be used instead of loading the map's .dat file. It is null unless
	 * supplied by a plugin.
	 *
	 * @param data NBTTagCompound data to load. If null, load from file
	 */
	public void setData(@Nullable Object data) {
		this.data = data;
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
