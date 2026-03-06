package com.playmonumenta.papermixins.util;

import com.playmonumenta.papermixins.duck.AdvancementAccess;
import net.minecraft.advancements.AdvancementNode;

public final class AdvancementNodeCompare {
    public static int compareAdvancementNodes(AdvancementNode o1, AdvancementNode o2) {
        // This used to be a one-line "Comparator.compares.thenComparing". You don't want to know what it looked like.
        // Also this can't just be a static method in the mixin class because of mixin rules
        AdvancementAccess access1 = Util.c(o1.holder().value());
        AdvancementAccess access2 = Util.c(o2.holder().value());
        int priorityDifference = access1.monumenta$getPriority() - access2.monumenta$getPriority();
        return priorityDifference != 0 ? priorityDifference : o1.holder().id().compareTo(o2.holder().id());
    }
}
