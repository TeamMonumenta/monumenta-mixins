package com.playmonumenta.papermixins.duck;

import com.playmonumenta.papermixins.itemindex.MonumentaInjectedItemData;

/**
 * Interface implemented by ItemStack in order to access the implementation.
 */
public interface ItemStackAccess {
	MonumentaInjectedItemData monumenta$getInjectedData();

	void monumenta$setInjectedData(MonumentaInjectedItemData data);
}
