package com.playmonumenta.mixinapi.item;

import com.playmonumenta.mixinapi.MonumentaPaperAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main entrypoint into this library.
 *
 * @author Floweynt
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface CustomItems {
	/**
	 * Obtains an instance of the API.
	 *
	 * @return The API.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	@NotNull
	static CustomItems getInstance() {
		return MonumentaPaperAPI.getInstance().getCustomItemsAPI();
	}

	@NotNull
	ItemStack create(@NotNull CustomItemType type, int count);

	/**
	 * Obtains the {@link CustomItem} associated with an {@link ItemStack}.
	 *
	 * @param stack The stack.
	 * @return The associated {@link ItemStack} or null if absent.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	@Nullable
	CustomItem getCustomItem(@NotNull ItemStack stack);

	/**
	 * Obtains the key associated with an {@link ItemStack}.
	 *
	 * @param stack The stack.
	 * @return The associated key or null if absent.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	@Nullable
	NamespacedKey getKey(@NotNull ItemStack stack);

	/**
	 * Obtains the variant associated with an {@link ItemStack}.
	 *
	 * @param stack The stack.
	 * @return The associated variant id or null if absent.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	@Nullable
	String getVariantId(@NotNull ItemStack stack);

	/**
	 * Forces re-computation of {@link ItemStack} properties.
	 *
	 * @param stack The stack to force update.
	 * @author Floweynt
	 * @since 1.0.0
	 */
	void forceUpdate(@NotNull ItemStack stack);
}
