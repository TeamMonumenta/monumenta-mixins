package com.playmonumenta.papermixins.impl.v1;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import com.playmonumenta.mixinapi.v1.DataFix;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;

public class DataFixImpl implements DataFix {
	private static final DataFixImpl INSTANCE = new DataFixImpl();

	public static DataFixImpl getInstance() {
		return INSTANCE;
	}

	private final Map<Types, DSL.TypeReference> map = ImmutableMap.<Types, DSL.TypeReference>builder()
		.put(Types.LEVEL, References.LEVEL)
		.put(Types.PLAYER, References.PLAYER)
		.put(Types.CHUNK, References.CHUNK)
		.put(Types.HOTBAR, References.HOTBAR)
		.put(Types.OPTIONS, References.OPTIONS)
		.put(Types.STRUCTURE, References.STRUCTURE)
		.put(Types.STATS, References.STATS)
		.put(Types.SAVED_DATA_COMMAND_STORAGE, References.SAVED_DATA_COMMAND_STORAGE)
		.put(Types.SAVED_DATA_FORCED_CHUNKS, References.SAVED_DATA_FORCED_CHUNKS)
		.put(Types.SAVED_DATA_MAP_DATA, References.SAVED_DATA_MAP_DATA)
		.put(Types.SAVED_DATA_MAP_INDEX, References.SAVED_DATA_MAP_INDEX)
		.put(Types.SAVED_DATA_RAIDS, References.SAVED_DATA_RAIDS)
		.put(Types.SAVED_DATA_RANDOM_SEQUENCES, References.SAVED_DATA_RANDOM_SEQUENCES)
		.put(Types.SAVED_DATA_STRUCTURE_FEATURE_INDICES, References.SAVED_DATA_STRUCTURE_FEATURE_INDICES)
		.put(Types.SAVED_DATA_SCOREBOARD, References.SAVED_DATA_SCOREBOARD)
		.put(Types.ADVANCEMENTS, References.ADVANCEMENTS)
		.put(Types.POI_CHUNK, References.POI_CHUNK)
		.put(Types.ENTITY_CHUNK, References.ENTITY_CHUNK)
		.put(Types.BLOCK_ENTITY, References.BLOCK_ENTITY)
		.put(Types.ITEM_STACK, References.ITEM_STACK)
		.put(Types.BLOCK_STATE, References.BLOCK_STATE)
		.put(Types.ENTITY_NAME, References.ENTITY_NAME)
		.put(Types.ENTITY_TREE, References.ENTITY_TREE)
		.put(Types.ENTITY, References.ENTITY)
		.put(Types.BLOCK_NAME, References.BLOCK_NAME)
		.put(Types.ITEM_NAME, References.ITEM_NAME)
		.put(Types.GAME_EVENT_NAME, References.GAME_EVENT_NAME)
		.put(Types.UNTAGGED_SPAWNER, References.UNTAGGED_SPAWNER)
		.put(Types.STRUCTURE_FEATURE, References.STRUCTURE_FEATURE)
		.put(Types.OBJECTIVE, References.OBJECTIVE)
		.put(Types.TEAM, References.TEAM)
		.put(Types.RECIPE, References.RECIPE)
		.put(Types.BIOME, References.BIOME)
		.put(Types.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, References.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
		.put(Types.WORLD_GEN_SETTINGS, References.WORLD_GEN_SETTINGS)
		.build();

	@Override
	public NBTContainer dataFix(NBTContainer input, Types type, int currentVersion, int targetVersion) {
		final var res = DataFixers.getDataFixer().update(
			map.get(type),
			new Dynamic<>(NbtOps.INSTANCE, (CompoundTag) input.getCompound()),
			currentVersion,
			targetVersion
		);

		return new NBTContainer(res.getValue());
	}
}
