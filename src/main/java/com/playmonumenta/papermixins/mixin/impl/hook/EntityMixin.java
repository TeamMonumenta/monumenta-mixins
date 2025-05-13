package com.playmonumenta.papermixins.mixin.impl.hook;

import com.playmonumenta.papermixins.duck.HookHolderAccess;
import com.playmonumenta.papermixins.impl.v1.hook.HolderBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements HookHolderAccess<org.bukkit.entity.Entity> {
	@Shadow
	public abstract CraftEntity getBukkitEntity();

	@Unique
	private final HolderBase.EntityHolder monumenta$hooks = new HolderBase.EntityHolder(getBukkitEntity());

	@Override
	public HolderBase<org.bukkit.entity.Entity> monumenta$getHookHolder() {
		return monumenta$hooks;
	}

	@Inject(
		method = "load",
		at = @At(value = "CONSTANT", args = "stringValue=Paper.FreezeLock")
	)
	private void loadCustom(CompoundTag nbt, CallbackInfo ci) {
		monumenta$hooks.deserialize(nbt.getCompound("monumenta:hooks"));
	}

	@Inject(
		method = "saveWithoutId(Lnet/minecraft/nbt/CompoundTag;Z)Lnet/minecraft/nbt/CompoundTag;",
		at = @At("RETURN")
	)
	private void saveCustom(CompoundTag rootTag, boolean includeAll, CallbackInfoReturnable<CompoundTag> cir) {
		rootTag.put("monumenta:hooks", monumenta$hooks.serialize());
	}

	@Inject(
		method = "baseTick",
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;firstTick:Z")
	)
	private void onTick(CallbackInfo ci) {
		monumenta$hooks.tick();
	}
}
