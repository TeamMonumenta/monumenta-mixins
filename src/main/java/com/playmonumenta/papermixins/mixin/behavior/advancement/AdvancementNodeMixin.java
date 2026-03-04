package com.playmonumenta.papermixins.mixin.behavior.advancement;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.AdvancementNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementNode.class)
public class AdvancementNodeMixin {
    @Shadow @Final private Set<AdvancementNode> children;

    @Inject(method = "children()Ljava/lang/Iterable;", at=@At("HEAD"), cancellable = true)
    private void getChildrenInject(CallbackInfoReturnable<Iterable<AdvancementNode>> cir) {
        List<AdvancementNode> l = children.stream().sorted(Comparator.comparing(o -> o.holder().id())).toList();
        cir.setReturnValue(l);
    }
}
