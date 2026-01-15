package com.playmonumenta.papermixins.mixin.impl.itemapi;

import com.playmonumenta.papermixins.duck.ItemStackAccess;
import com.playmonumenta.papermixins.itemindex.MonumentaInjectedItemData;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Debug(export = true)
@Mixin(ItemStack.class)
public class BukkitItemStackMixin implements ItemStackAccess {
	@Unique
	private MonumentaInjectedItemData injectedData;

	@Unique
	public void monumenta$setInjectedData(MonumentaInjectedItemData data) {
		System.out.println("saved, data is " + (data == null ? "" : "not ") + "null");
		this.injectedData = data;
		System.out.println("data is now " + (injectedData == null ? "" : "not ") + "null");
		if (injectedData != null) {
			System.out.println(injectedData);
		}
	}

	@Unique
	public MonumentaInjectedItemData monumenta$getInjectedData() {
		System.out.println("loading, data is " + (injectedData == null ? "" : "not ") + "null");
		return injectedData;
	}
}
