package com.playmonumenta.papermixins.mixin.behavior.enchant;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0013-Monumenta-Make-armor-unbreaking-work-the-same-as-too.patch
 * <p>
 * Remove armor unbreaking quirks.
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
	// TODO
}
