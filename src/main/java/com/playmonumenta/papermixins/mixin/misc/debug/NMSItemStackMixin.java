package com.playmonumenta.papermixins.mixin.misc.debug;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class NMSItemStackMixin {
    @Shadow @Final private static Logger LOGGER;

    // TODO: this may cause crashes in some cases
    @Inject(
            method = "setItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void deprecateSetItem(Item item, CallbackInfo ci) {
        ci.cancel();
        LOGGER.error("setItem() has been deprecated and should not be used, try cloning the stack instead.");
        LOGGER.error("This call has been ignored, since setting items directly interferes with Custom Item API logic");
        LOGGER.error("Please fix or nag the plugin developer to fix: ", new UnsupportedOperationException());
    }
}
