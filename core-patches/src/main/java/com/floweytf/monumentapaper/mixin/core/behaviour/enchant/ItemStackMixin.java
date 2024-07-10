package com.floweytf.monumentapaper.mixin.core.behaviour.enchant;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * @author Flowey
 * @mm-patch 0013-Monumenta-Make-armor-unbreaking-work-the-same-as-too.patch
 * <p>
 * Remove armor unbreaking quirks
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    @ModifyVariable(
        method = "hurt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel" +
                "(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I",
            shift = At.Shift.AFTER
        ),
        argsOnly = true,
        index = 1
    )
    private int monumenta$curveAmount(int amount) {
        return (int) Math.min(amount, Math.sqrt(amount * 4));
    }
}