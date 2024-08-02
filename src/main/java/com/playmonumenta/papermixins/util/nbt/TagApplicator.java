package com.playmonumenta.papermixins.util.nbt;

import net.minecraft.nbt.Tag;

public interface TagApplicator {
    void put(String key, Tag tag);
}
