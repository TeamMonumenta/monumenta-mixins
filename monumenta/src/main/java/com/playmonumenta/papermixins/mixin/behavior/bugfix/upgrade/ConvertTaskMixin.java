package com.playmonumenta.papermixins.mixin.behavior.bugfix.upgrade;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.duck.WorldInfoAccess;
import com.playmonumenta.papermixins.util.Util;
import io.papermc.paper.world.ThreadedWorldUpgrader;
import java.io.IOException;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedWorldUpgrader.ConvertTask.class)
public class ConvertTaskMixin {
	@Unique
	private static final Codec<List<BlockState>> MONUMENTA$PALETTE_CODEC = BlockState.CODEC.listOf();

	@Shadow
	@Final
	private ThreadedWorldUpgrader.WorldInfo worldInfo;

	@Inject(
		method = "run",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;",
			remap = false
		)
	)
	private void upgradeEntity(CallbackInfo ci, @Local ChunkPos pos) throws IOException {
		if (!ConfigManager.getConfig().behavior.forceUpgradeIncludeEntities) {
			return;
		}

		@SuppressWarnings("resource") //
		final var entityRegion = Util.<WorldInfoAccess>c(worldInfo).monumenta$getRegion();

		final var data = entityRegion.read(pos);

		if (data == null) {
			return;
		}

		final var dataVersion = data.getInt("DataVersion");
		final var targetVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

		if (dataVersion >= targetVersion) {
			return;
		}

		ThreadedWorldUpgrader.LOGGER.info("[monumenta] upgrading entity chunk @({}, {}) {} -> {}", pos.x, pos.z,
			dataVersion, targetVersion);
		final var update = DataFixTypes.ENTITY_CHUNK.update(DataFixers.getDataFixer(), data, dataVersion,
			targetVersion);
		update.putInt("DataVersion", targetVersion);
		entityRegion.write(pos, update);
	}

	@ModifyVariable(
		method = "run",
		at = @At("STORE"),
		name = "modified"
	)
	private boolean upgradeBlockStates(boolean modified, @Local CompoundTag chunkNBT) {
		if (!ConfigManager.getConfig().behavior.forceUpgradeEagerBlockStates) {
			return modified;
		}

		for (Tag tag : chunkNBT.getList("sections", CompoundTag.TAG_COMPOUND)) {
			final var states = ((CompoundTag) tag).getCompound("block_states");
			final var oldPalette = states.getList("palette", Tag.TAG_COMPOUND);

			final var blockStateList = MONUMENTA$PALETTE_CODEC.decode(NbtOps.INSTANCE, oldPalette)
				.getOrThrow(false, string -> {
				})
				.getFirst();

			final var newPalette = BlockState.CODEC.listOf().encodeStart(NbtOps.INSTANCE, blockStateList)
				.getOrThrow(false, string -> {
				});

			if (!newPalette.equals(oldPalette)) {
				modified = true;
			}

			states.put("palette", newPalette);
		}

		return modified;
	}
}
