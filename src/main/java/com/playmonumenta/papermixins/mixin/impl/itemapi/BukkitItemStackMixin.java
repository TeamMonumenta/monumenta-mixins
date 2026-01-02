package com.playmonumenta.papermixins.mixin.impl.itemapi;

import com.playmonumenta.papermixins.duck.ItemStackAccess;
import com.playmonumenta.papermixins.itemindex.MonumentaInjectedItemData;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class BukkitItemStackMixin implements ItemStackAccess {
	@Unique
	private MonumentaInjectedItemData injectedData;

	@Unique
	public void monumenta$setInjectedData(MonumentaInjectedItemData data) {
		injectedData = data;
	}

	@Unique
	public MonumentaInjectedItemData monumenta$getInjectedData() {
		return injectedData;
	}
}
