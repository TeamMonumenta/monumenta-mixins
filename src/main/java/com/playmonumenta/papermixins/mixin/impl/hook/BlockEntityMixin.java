package com.playmonumenta.papermixins.mixin.impl.hook;

import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.duck.HookHolderAccess;
import com.playmonumenta.papermixins.impl.v1.hook.HolderBase;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlockStates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements HookHolderAccess<TileState> {
	@Shadow
	@Nullable
	protected Level level;
	@Shadow
	public abstract BlockEntityType<?> getType();
	@Shadow
	@Nullable
	public abstract Level getLevel();
	@Shadow
	@Final
	protected BlockPos worldPosition;
	@Shadow
	private BlockState blockState;

	@Unique
	private final HolderBase.BlockEntityHolder monumenta$hooks = new HolderBase.BlockEntityHolder(
		// TODO: figure out better API for this
		(TileState) CraftBlockStates.FACTORIES_BY_BLOCK_ENTITY_TYPE.get(getType()).createBlockState(
			Optional.ofNullable(getLevel()).map(Level::getWorld).orElse(null),
			worldPosition,
			blockState,
			(BlockEntity) (Object) this
		)
	);

	@Override
	public HolderBase<TileState> monumenta$getHookHolder() {
		return monumenta$hooks;
	}

	@Inject(
		method = "load",
		at = @At(value = "RETURN")
	)
	private void loadCustom(CompoundTag nbt, CallbackInfo ci) {
		monumenta$hooks.deserialize(nbt.getCompound("monumenta:hooks"));
	}

	@Inject(
		method = "saveWithoutMetadata",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveAdditional" +
			"(Lnet/minecraft/nbt/CompoundTag;)V")
	)
	private void saveCustom(CallbackInfoReturnable<CompoundTag> cir, @Local CompoundTag tag) {
		tag.put("monumenta:hooks", monumenta$hooks.serialize());
	}
}
