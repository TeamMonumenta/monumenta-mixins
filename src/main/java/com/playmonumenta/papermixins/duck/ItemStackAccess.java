package com.playmonumenta.papermixins.duck;

import com.playmonumenta.papermixins.itemindex.MonumentaInjectedItemData;
import com.playmonumenta.papermixins.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Interface implemented by ItemStack in order to access the implementation.
 */
public interface ItemStackAccess {
	MonumentaInjectedItemData monumenta$getInjectedData();

	void monumenta$setInjectedData(MonumentaInjectedItemData data);
}
