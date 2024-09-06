package com.playmonumenta.mixinapi.v1.resource;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * API for defining datapack loaders plugins, with some limitations.
 *
 * @author Floweynt
 * @since 1.0.0
 */
@ApiStatus.OverrideOnly
public interface DatapackResourceLoader {
	/**
	 * Handler for datapack loading.
	 *
	 * @param manager The datapack loading manager, which contains the resources being loaded.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	void load(@NotNull DatapackResourceManager manager);

	/**
	 * Whether this datapack loader can be reloaded (with /reload) or not.
	 * If reloads are disabled, calls to {@link DatapackResourceLoader#load(DatapackResourceManager)} will not be
	 * called on /reload, and will only be initialized once.
	 *
	 * @return Whether reloads are allowed.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	default boolean canReload() {
		return false;
	}
}
