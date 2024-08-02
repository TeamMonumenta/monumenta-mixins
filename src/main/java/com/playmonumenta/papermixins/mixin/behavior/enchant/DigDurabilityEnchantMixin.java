package com.playmonumenta.papermixins.mixin.behavior.enchant;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0013-Monumenta-Make-armor-unbreaking-work-the-same-as-too.patch
 * <p>
 * Remove armor unbreaking quirks.
 */
@Mixin(DigDurabilityEnchantment.class)
public class DigDurabilityEnchantMixin {
    /**
     * @author Flowey
     * @reason All items work like tools for unbreaking.
     */
    @Overwrite
    public static boolean shouldIgnoreDurabilityDrop(ItemStack item, int level, RandomSource random) {
        return random.nextInt(level + 1) > 0;
    }
}
