package com.playmonumenta.papermixins.mixin.behavior.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerBlockEntityMixin extends BlockEntity {

	@Shadow @Nullable public abstract Component getCustomName();

	protected BaseContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// Chunk Load
	@Override
	public @NotNull CompoundTag getUpdateTag() {
		final CompoundTag tag = new CompoundTag();
		Component name = getCustomName();
		if (name != null) {
			tag.putString("CustomName", Component.Serializer.toJson(name));
		}
		return tag;
	}

	// Incremental Update
	@Override
	public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	// On Player Placement
	@Inject(method = "setCustomName", at = @At("TAIL"))
	private void papermixins$broadcastCustomName(@Nullable Component name, CallbackInfo ci) {
		if (this.level != null && !this.level.isClientSide) {
			this.setChanged();
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
			this.level.blockEntityChanged(this.worldPosition);
		}
	}
}
