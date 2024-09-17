package com.playmonumenta.papermixins.mixin.behavior.enchant;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    @Inject(
        method = "shouldIgnoreDurabilityDrop",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void shouldIgnoreDurabilityDrop(ItemStack item, int level, RandomSource random,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (MonumentaMod.getConfig().behavior.normalizeArmourUnbreaking) {
            cir.setReturnValue(random.nextInt(level + 1) > 0);
        }
    }
}
