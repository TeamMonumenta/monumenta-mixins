package com.playmonumenta.papermixins.mixin.bugfix.upgrade;

import com.google.common.collect.ObjectArrays;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.duck.WorldInfoAccess;
import io.papermc.paper.world.ThreadedWorldUpgrader;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThreadedWorldUpgrader.class)
public class ThreadedWorldUpgraderMixin {
	@WrapOperation(
		method = "convert",
		at = @At(
			value = "INVOKE",
			target = "Ljava/io/File;listFiles(Ljava/io/FilenameFilter;)[Ljava/io/File;"
		)
	)
	private File[] addEntityOnlyChunksToRegionList(
		File instance, FilenameFilter filter, Operation<File[]> original, @Local(ordinal = 0) File worldFolder,
		@Share("chunkPosSet") LocalRef<Set<ChunkPos>> chunkPosSet
	) {
		if (!MonumentaMod.getConfig().behavior.forceUpgradeIncludeEntities) {
			return original.call(instance, filter);
		}

		chunkPosSet.set(new HashSet<>());

		return ObjectArrays.concat(
			original.call(instance, filter),
			original.call(new File(worldFolder, "entities"), filter),
			File.class
		);
	}

	@ModifyExpressionValue(
		method = "convert",
		at = @At(
			value = "NEW",
			target = "(Ljava/util/function/Supplier;Lnet/minecraft/world/level/chunk/storage/ChunkStorage;" +
				"ZLnet/minecraft/resources/ResourceKey;Ljava/util/Optional;)" +
				"Lio/papermc/paper/world/ThreadedWorldUpgrader$WorldInfo;"
		)
	)
	private ThreadedWorldUpgrader.WorldInfo addEntityChunkLoader(ThreadedWorldUpgrader.WorldInfo original,
																@Local(ordinal = 0) File worldFolder) {
		if (MonumentaMod.getConfig().behavior.forceUpgradeIncludeEntities) {
			final var path = worldFolder.toPath().resolve("entities");

			ThreadedWorldUpgrader.LOGGER.info("[monumenta] entity region is {}", path);

			((WorldInfoAccess) (Object) original).monumenta$setRegion(new ServerLevel.EntityRegionFileStorage(
				worldFolder.toPath().resolve("entities"),
				true
			));
		}

		return original;
	}

	@ModifyExpressionValue(
		method = "convert",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/chunk/storage/RegionFileStorage;getRegionFileCoordinates" +
				"(Ljava/nio/file/Path;)Lnet/minecraft/world/level/ChunkPos;"
		)
	)
	private ChunkPos deduplicateChunks(ChunkPos chunkPos, @Share("chunkPosSet") LocalRef<Set<ChunkPos>> chunkPosSet) {
		if (!MonumentaMod.getConfig().behavior.forceUpgradeIncludeEntities) {
			return chunkPos;
		}

		final var set = chunkPosSet.get();

		if (set.contains(chunkPos)) {
			return null;
		}
		set.add(chunkPos);
		return chunkPos;
	}
}
