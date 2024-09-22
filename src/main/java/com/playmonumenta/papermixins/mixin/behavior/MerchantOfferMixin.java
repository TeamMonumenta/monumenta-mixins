package com.playmonumenta.papermixins.mixin.behavior;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Flowey
 * @mm-patch 0026-Monumenta-Improve-Villager-Trade-Comparison.patch
 */
@Mixin(MerchantOffer.class)
public class MerchantOfferMixin {
	@Unique
	private void monumenta$popDisplayIfPlain(ItemStack itemStack) {
		CompoundTag display = itemStack.getTagElement("display");
		if (display != null) {
			CompoundTag plain = itemStack.getTagElement("plain");
			if (plain != null) {
				if (plain.contains("Name", 8)) {
					display.remove("Name");
				}
				if (plain.contains("Lore", 9)) {
					display.remove("Lore");
				}
			}
		}
	}

	/**
	 * @author Flowey
	 * @reason Implement Logic.
	 */
	@Overwrite
	private boolean isRequiredItem(ItemStack given, ItemStack sample) {
		if (sample.isEmpty() && given.isEmpty()) {
			return true;
		} else {
			ItemStack originalCopy = given.copy();
			ItemStack sampleCopy = sample.copy();

			if (originalCopy.getItem().canBeDepleted() && sampleCopy.getItem().canBeDepleted()) {
				sampleCopy.setDamageValue(sampleCopy.getDamageValue());
			}

			if (originalCopy.hasTag()) {
				monumenta$popDisplayIfPlain(originalCopy);
			}

			if (sampleCopy.hasTag()) {
				monumenta$popDisplayIfPlain(sampleCopy);
			}

			boolean tagMatches =
				!sampleCopy.hasTag() || originalCopy.hasTag() && NbtUtils.compareNbt(sampleCopy.getTag(),
					originalCopy.getTag(), false);
			return ItemStack.isSameItem(originalCopy, sampleCopy) && tagMatches;
		}
	}
}
