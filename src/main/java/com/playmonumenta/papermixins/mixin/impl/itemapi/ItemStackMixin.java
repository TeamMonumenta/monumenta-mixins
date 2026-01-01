package com.playmonumenta.papermixins.mixin.impl.itemapi;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.playmonumenta.papermixins.duck.ItemStackAccess;
import com.playmonumenta.papermixins.items.ItemStackStateManager;
import com.playmonumenta.papermixins.util.Util;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.bukkit.craftbukkit.v1_20_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttribute;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackAccess {
	@Unique
	private final ItemStackStateManager monumenta$stateManager = new ItemStackStateManager();

	@Shadow
	@javax.annotation.Nullable
	private Item item;

	@Shadow @Final private static Logger LOGGER;

	// TODO: this may cause crashes in some cases
	@Inject(
		method = "setItem",
		at = @At("HEAD"),
		cancellable = true
	)
	private void deprecateSetItem(Item item, CallbackInfo ci) {
		ci.cancel();
		LOGGER.error("setItem() has been deprecated and should not be used, try cloning the stack instead.");
		LOGGER.error("This call has been ignored, since setting items directly interferes with Custom Item API logic");
		LOGGER.error("Please fix or nag the plugin developer to fix: ", new UnsupportedOperationException());
	}

	@Override
	public @Nullable ItemStackStateManager monumenta$getStateManager() {
		return monumenta$stateManager;
	}

	@Override
	public void monumenta$setItemRaw(Item item) {
		this.item = item;
	}

	@ModifyReturnValue(
		at = @At("RETURN"),
		method = "getAttributeModifiers"
	)
	public final Multimap<Attribute, AttributeModifier> addAttributes(Multimap<Attribute, AttributeModifier> returnValue, EquipmentSlot slot) {
		final var state = monumenta$stateManager.getCustomState();
		if (state == null)
			return returnValue;

		for (var attr : state.item().getBaseAttributes().entries()) {
			if (CraftEquipmentSlot.getNMS(Objects.requireNonNull(attr.getValue().getSlot())) != slot)
				continue;
			returnValue.put(
				CraftAttribute.bukkitToMinecraft(attr.getKey()),
				CraftAttributeInstance.convert(attr.getValue())
			);
		}

		return returnValue;
	}
}
