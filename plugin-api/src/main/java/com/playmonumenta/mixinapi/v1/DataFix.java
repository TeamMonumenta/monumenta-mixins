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
		SAVED_DATA_COMMAND_STORAGE,
		SAVED_DATA_FORCED_CHUNKS,
		SAVED_DATA_MAP_DATA,
		SAVED_DATA_MAP_INDEX,
		SAVED_DATA_RAIDS,
		SAVED_DATA_RANDOM_SEQUENCES,
		SAVED_DATA_STRUCTURE_FEATURE_INDICES,
		SAVED_DATA_SCOREBOARD,
		ADVANCEMENTS,
		POI_CHUNK,
		ENTITY_CHUNK,
		BLOCK_ENTITY,
		ITEM_STACK,
		BLOCK_STATE,
		ENTITY_NAME,
		ENTITY_TREE,
		ENTITY,
		BLOCK_NAME,
		ITEM_NAME,
		GAME_EVENT_NAME,
		UNTAGGED_SPAWNER,
		STRUCTURE_FEATURE,
		OBJECTIVE,
		TEAM,
		RECIPE,
		BIOME,
		MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST,
		WORLD_GEN_SETTINGS,
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
