package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.playmonumenta.papermixins.duck.AdvancementAccess;
import com.playmonumenta.papermixins.util.Util;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.TreeNodePosition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TreeNodePosition.class)
public class TreeNodePositionMixin {
    @Shadow
    @Final
    private AdvancementNode node;

    @WrapOperation(method = "lambda$finalizePosition$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;setLocation(FF)V"))
    private void adjustPosition(DisplayInfo instance, float x, float y, Operation<Void> original) {
        AdvancementAccess access = Util.c(node.advancement());
        String type = access.monumenta$getTreePositionType();
        float x2 = access.monumenta$getTreePositionX();
        float y2 = access.monumenta$getTreePositionY();
        if (type.equals("absolute")) {
            original.call(instance, x2, y2);
        } else { // relative
            original.call(instance, x + x2, y + y2);
        }
    }
}
