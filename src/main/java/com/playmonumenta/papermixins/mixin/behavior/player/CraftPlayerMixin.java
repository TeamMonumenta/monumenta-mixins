package com.playmonumenta.papermixins.mixin.behavior.player;

import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftPlayer.class)
public class CraftPlayerMixin {
    @Redirect(method = "setSpectatorTarget", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Preconditions;checkArgument(ZLjava/lang/Object;)V"))
    private static void removeSpectatorCheck(boolean expression, Object errorMessage) {

    }
}
