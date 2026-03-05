package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.playmonumenta.papermixins.util.AdvancementNodeCompare;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.AdvancementNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementNode.class)
public class AdvancementNodeMixin {
    @Shadow @Final private Set<AdvancementNode> children;

    @Unique @Nullable
    List<AdvancementNode> monumenta$cachedChildren;

    @Inject(method = "addChild(Lnet/minecraft/advancements/AdvancementNode;)V", at = @At("HEAD"))
    private void addChildInject(AdvancementNode ignored, CallbackInfo ci) {
        monumenta$cachedChildren = null; // invalidate cached sort
    }

    @Inject(method = "children()Ljava/lang/Iterable;", at=@At("HEAD"), cancellable = true)
    private void getChildrenInject(CallbackInfoReturnable<Iterable<AdvancementNode>> cir) {
        if (monumenta$cachedChildren == null) {
            monumenta$cachedChildren = children.stream().sorted(AdvancementNodeCompare::compareAdvancementNodes).toList();
        }
        cir.setReturnValue(monumenta$cachedChildren);
    }
}
