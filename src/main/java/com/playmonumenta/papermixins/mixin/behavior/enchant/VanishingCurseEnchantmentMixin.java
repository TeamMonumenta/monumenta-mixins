package com.playmonumenta.papermixins.mixin.behavior.enchant;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.VanishingCurseEnchantment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Flowey
 * @mm-patch 0012-Monumenta-Max-level-for-curse-of-vanishing-is-2.patch
 * <p>
 * Change curse of vanishing max level to 2.
 */
@Mixin(VanishingCurseEnchantment.class)
public class VanishingCurseEnchantmentMixin extends Enchantment {
	protected VanishingCurseEnchantmentMixin(Rarity rarity, EnchantmentCategory target, EquipmentSlot[] slotTypes) {
		super(rarity, target, slotTypes);
	}

	// Mixin @Overrides will be properly mixed into the target class, which is convenient.
	@Override
	public int getMaxLevel() {
		return MonumentaMod.getConfig().behavior.curseOfVanishingMaxLevel;
	}
}
