package com.playmonumenta.papermixins.impl.v1.item;

import com.google.common.base.Preconditions;
import com.playmonumenta.mixinapi.v1.item.CustomItem;
import com.playmonumenta.mixinapi.v1.item.CustomItemType;
import com.playmonumenta.mixinapi.v1.item.CustomItems;
import com.playmonumenta.papermixins.duck.ItemStackAccess;
import com.playmonumenta.papermixins.items.CustomItemAPIMain;
import com.playmonumenta.papermixins.items.CustomItemInstance;
import com.playmonumenta.papermixins.util.Util;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CustomItemsImpl implements CustomItems {
	private static final CustomItemsImpl INSTANCE = new CustomItemsImpl();

	private CustomItemsImpl() {

	}

	public static CustomItemsImpl getInstance() {
		return INSTANCE;
	}

	@Override
	@NotNull
	public ItemStack create(@NotNull CustomItemType type, int count) {
		Preconditions.checkNotNull(type);

		// TODO: need a way of ensuring metadata is never messed up
		// this is easier with 1.20.5+
		return CraftItemStack.asCraftMirror(
			CustomItemAPIMain.makeItem(CraftNamespacedKey.toMinecraft(type.variantSet().key()), type.variantId(), count,
				Optional.empty())
		);
	}

	@Override
	@Nullable
	public CustomItem getCustomItem(@NotNull ItemStack stack) {
		return getItemProperty(stack, CustomItemInstance::item);
	}

	@Override
	@Nullable
	public NamespacedKey getKey(@NotNull ItemStack stack) {
		return getItemProperty(stack, CustomItemInstance::key);
	}

	@Override
	@Nullable
	public String getVariantId(@NotNull ItemStack stack) {
		return getItemProperty(stack, CustomItemInstance::variant);
	}

	@SuppressWarnings("UnreachableCode")
	@Override
	public void forceUpdate(@NotNull ItemStack stack) {
		Preconditions.checkNotNull(stack);

		if (stack instanceof CraftItemStack craftStack) {
			final var mc = craftStack.handle;
			final var state = ItemStackAccess.get(mc).monumenta$getStateManager();
			if (state.getCustomState() != null) {
				state.recomputeDisplay(craftStack.handle);
			}
		} else {
			throw new IllegalArgumentException("stack must be from paper");
		}
	}

	@Nullable
	private <T> T getItemProperty(ItemStack stack, Function<CustomItemInstance, T> getter) {
		Preconditions.checkNotNull(stack);

		if (stack instanceof CraftItemStack craftStack) {
			final var mc = craftStack.handle;
			return Util.mapNull(ItemStackAccess.get(mc).monumenta$getStateManager().getCustomState(), getter);
		} else {
			throw new IllegalArgumentException("stack must be from paper");
		}
	}
}
