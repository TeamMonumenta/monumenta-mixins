package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.playmonumenta.papermixins.duck.AdvancementAccess;
import com.playmonumenta.papermixins.util.Util;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.resources.ResourceLocation;
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
            monumenta$cachedChildren = children.stream().sorted(
                    // There used to be a one-line "Comparator.compares.thenComparing" here. You don't want to know what it looked like.
                    (o1, o2) -> {
                        AdvancementAccess access1 = Util.c(o1.holder().value());
                        AdvancementAccess access2 = Util.c(o2.holder().value());
                        int priorityDifference = access1.monumenta$getPriority() - access2.monumenta$getPriority();
                        return priorityDifference != 0 ? priorityDifference : o1.holder().id().compareTo(o2.holder().id());
                    }
            ).toList();
        }
        cir.setReturnValue(monumenta$cachedChildren);
    }
}
