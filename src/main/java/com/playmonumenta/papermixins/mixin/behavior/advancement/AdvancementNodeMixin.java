package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.playmonumenta.papermixins.duck.AdvancementAccess;
import com.playmonumenta.papermixins.duck.hook.AdvancementNodeAccess;
import com.playmonumenta.papermixins.util.Util;
import java.util.Collections;
import java.util.PriorityQueue;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementNode.class)
public class AdvancementNodeMixin implements AdvancementNodeAccess {
    @Shadow @Final private AdvancementHolder holder;
    @Unique
    private static final AdvancementComparator monumenta$comparator = new AdvancementComparator();

    @Unique
    private final PriorityQueue<AdvancementNode> monumenta$orderedChildren = new PriorityQueue<>(monumenta$comparator);

    @Inject(method = "addChild(Lnet/minecraft/advancements/AdvancementNode;)V", at = @At("HEAD"), cancellable = true)
    public void addChildInject(AdvancementNode advancement, CallbackInfo ci) {
        monumenta$orderedChildren.add(advancement);
        System.out.println("adding " + advancement.holder().id());
        ci.cancel();
    }

    @Inject(method = "children()Ljava/lang/Iterable;", at=@At("HEAD"), cancellable = true)
    private void getChildrenInject(CallbackInfoReturnable<Iterable<AdvancementNode>> cir) {
        cir.setReturnValue(monumenta$orderedChildren);
//        System.out.println("sorting " + holder.id() + " children: ");
//        for (AdvancementNode a : monumenta$orderedChildren) {
//            System.out.println(a.holder().id());
//        }
//        System.out.println("--- END ---");
    }

    @Unique
    public int monumenta$getPriority() {
        return 0;
        // return Util.<AdvancementAccess>c(holder.value()).monumenta$getPriority();
    }
}
