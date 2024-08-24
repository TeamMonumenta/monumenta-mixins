package com.playmonumenta.papermixins.duck;

import com.playmonumenta.papermixins.items.ItemStackStateManager;
import com.playmonumenta.papermixins.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Interface implemented by ItemStack in order to access the implementation.
 */
public interface ItemStackAccess {
	/**
	 * Obtains the contained state manager.
	 *
	 * @return The state manager bound to an ItemStack.
	 */
	ItemStackStateManager monumenta$getStateManager();

	/**
	 * Sets the item raw. This is needed because setItem throws.
	 *
	 * @param item The item to set.
	 */
	void monumenta$setItemRaw(Item item);

	static ItemStackAccess get(ItemStack stack) {
		return Util.c(stack);
	}

	static ItemStackStateManager stateManager(ItemStack stack) {
		return get(stack).monumenta$getStateManager();
	}

	static void setItemRaw(ItemStack stack, Item item) {
		get(stack).monumenta$setItemRaw(item);
	}
}
