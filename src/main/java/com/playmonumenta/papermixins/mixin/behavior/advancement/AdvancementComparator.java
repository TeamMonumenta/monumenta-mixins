package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.playmonumenta.papermixins.duck.hook.AdvancementNodeAccess;
import com.playmonumenta.papermixins.util.Util;
import java.util.Comparator;
import net.minecraft.advancements.AdvancementNode;

public class AdvancementComparator implements Comparator<AdvancementNode> {
    @Override
    public int compare(AdvancementNode o1, AdvancementNode o2) {
        int a = Util.<AdvancementNodeAccess>c(o1).monumenta$getPriority() - Util.<AdvancementNodeAccess>c(o2).monumenta$getPriority();
        if (a != 0) {
            return a;
        }
        int b = o1.holder().id().compareTo(o2.holder().id());
        // System.out.println("comparing "+o1.holder().id() + " " + o2.holder().id()+", the answer is "+b);
        return b;
    }
}
