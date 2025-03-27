package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0020-Monumenta-Clear-Crafting-Slots-when-Clearing.patch
 * <p>
 * Clear Crafting Slots when Clearing
 */
@Mixin(Inventory.class)
public class InventoryMixin {
	@Shadow
	@Final
	public Player player;

	@ModifyReturnValue(method = "clearOrCountMatchingItems", at = @At("TAIL"))
	private int clearCraftingSlots(int original, Predicate<ItemStack> shouldRemove, int maxCount,
								Container craftingInventory) {
		CraftingContainer container = null;

		if (this.player.containerMenu instanceof InventoryMenu)
			container = ((InventoryMenu) this.player.containerMenu).getCraftSlots();
		else if (this.player.containerMenu instanceof CraftingMenu)
			container = ((CraftingMenu) this.player.containerMenu).craftSlots;

		if (container != null) {
			for (int slotNum = 0; slotNum < container.getContainerSize(); ++slotNum) {
				ItemStack item = container.getItem(slotNum);

				if (!item.isEmpty() && shouldRemove.test(item)) {
					int countFromStack = maxCount <= 0 ? item.getCount() : Math.min(maxCount - original,
						item.getCount());

					original += countFromStack;
					if (maxCount != 0) {
						item.shrink(countFromStack);
						if (item.isEmpty()) {
							container.setItem(slotNum, ItemStack.EMPTY);
						}

						if (maxCount > 0 && original >= maxCount) {
							return original;
						}
					}
				}
			}
		}

		return original;
	}
}
