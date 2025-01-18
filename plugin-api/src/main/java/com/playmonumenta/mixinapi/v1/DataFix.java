package com.playmonumenta.mixinapi.v1;

import de.tr7zw.nbtapi.NBTContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface DataFix {
	enum Types {
		LEVEL,
		PLAYER,
		CHUNK,
		HOTBAR,
		OPTIONS,
		STRUCTURE,
		STATS,
		ADVANCEMENTS,
		POI_CHUNK,
		ENTITY_CHUNK,
		TILE_ENTITY,
		ITEM_STACK,
		BLOCK_STATE,
		ENTITY,
		UNTAGGED_SPAWNER,
		STRUCTURE_FEATURE,
		OBJECTIVE,
		TEAM,
		WORLD_GEN_SETTINGS,
		SAVED_DATA_RANDOM_SEQUENCES,
		SAVED_DATA_SCOREBOARD,
		SAVED_DATA_STRUCTURE_FEATURE_INDICES,
		SAVED_DATA_MAP_DATA,
		SAVED_DATA_RAIDS,
	}

	/**
	 * Obtains an instance of the API.
	 *
	 * @return The API.
	 * @author Floweynt
	 * @since 1.0.3
	 */
	@NotNull
	static DataFix getInstance() {
		return MonumentaPaperAPI.getInstance().getDataFix();
	}

	NBTContainer dataFix(NBTContainer input, Types type, int currentVersion, int targetVersion);

	int currentDataVersion();
}
