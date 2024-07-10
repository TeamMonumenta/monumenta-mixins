package com.floweytf.customitemapi.mixin;

import de.tr7zw.nbtapi.plugin.NBTAPI;
import de.tr7zw.nbtapi.plugin.tests.Test;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(NBTAPI.class)
public class NBTAPIMixin {
    @Redirect(
        method = "onLoad",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
        )
    )
    private boolean custom_item_api$disableNBTAPITests(List<Test> instance, Object e) {
        return false;
    }
}
