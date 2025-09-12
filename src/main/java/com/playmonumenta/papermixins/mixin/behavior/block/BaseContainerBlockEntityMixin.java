package com.playmonumenta.papermixins.mixin.behavior.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerBlockEntityMixin extends BlockEntity {
	@Shadow
	@Nullable
	public abstract Component getCustomName();

	public BaseContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		final CompoundTag tag = new CompoundTag();
		Component name = getCustomName();
		if (name != null) {
			tag.putString("CustomName", Component.Serializer.toJson(name));
		}

		return tag;
	}
}
