package com.playmonumenta.papermixins.impl.v1;

import ca.spottedleaf.dataconverter.minecraft.MCDataConverter;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCDataType;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import com.google.common.collect.ImmutableMap;
import com.playmonumenta.mixinapi.v1.DataFix;
import de.tr7zw.nbtapi.NBTContainer;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;

public class DataFixImpl implements DataFix {
	private static final DataFixImpl INSTANCE = new DataFixImpl();

	public static DataFixImpl getInstance() {
		return INSTANCE;
	}

	private final Map<Types, MCDataType> map = ImmutableMap.<Types, MCDataType>builder()
		.put(Types.LEVEL, MCTypeRegistry.LEVEL)
		.put(Types.PLAYER, MCTypeRegistry.PLAYER)
		.put(Types.CHUNK, MCTypeRegistry.CHUNK)
		.put(Types.HOTBAR, MCTypeRegistry.HOTBAR)
		.put(Types.OPTIONS, MCTypeRegistry.OPTIONS)
		.put(Types.STRUCTURE, MCTypeRegistry.STRUCTURE)
		.put(Types.STATS, MCTypeRegistry.STATS)
		.put(Types.ADVANCEMENTS, MCTypeRegistry.ADVANCEMENTS)
		.put(Types.POI_CHUNK, MCTypeRegistry.POI_CHUNK)
		.put(Types.ENTITY_CHUNK, MCTypeRegistry.ENTITY_CHUNK)
		.put(Types.TILE_ENTITY, MCTypeRegistry.TILE_ENTITY)
		.put(Types.ITEM_STACK, MCTypeRegistry.ITEM_STACK)
		.put(Types.BLOCK_STATE, MCTypeRegistry.BLOCK_STATE)
		.put(Types.ENTITY, MCTypeRegistry.ENTITY)
		.put(Types.UNTAGGED_SPAWNER, MCTypeRegistry.UNTAGGED_SPAWNER)
		.put(Types.STRUCTURE_FEATURE, MCTypeRegistry.STRUCTURE_FEATURE)
		.put(Types.OBJECTIVE, MCTypeRegistry.OBJECTIVE)
		.put(Types.TEAM, MCTypeRegistry.TEAM)
		.put(Types.WORLD_GEN_SETTINGS, MCTypeRegistry.WORLD_GEN_SETTINGS)
		.put(Types.SAVED_DATA_RANDOM_SEQUENCES, MCTypeRegistry.SAVED_DATA_RANDOM_SEQUENCES)
		.put(Types.SAVED_DATA_SCOREBOARD, MCTypeRegistry.SAVED_DATA_SCOREBOARD)
		.put(Types.SAVED_DATA_STRUCTURE_FEATURE_INDICES, MCTypeRegistry.SAVED_DATA_STRUCTURE_FEATURE_INDICES)
		.put(Types.SAVED_DATA_MAP_DATA, MCTypeRegistry.SAVED_DATA_MAP_DATA)
		.put(Types.SAVED_DATA_RAIDS, MCTypeRegistry.SAVED_DATA_RAIDS)
		.build();

	@Override
	public NBTContainer dataFix(NBTContainer input, Types type, int currentVersion, int targetVersion) {
		final var res = MCDataConverter.convertTag(
			map.get(type),
			(CompoundTag) input.getCompound(),
			currentVersion,
			targetVersion
		);

		return new NBTContainer(res);
	}

	@Override
	public int currentDataVersion() {
		return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
	}
}
