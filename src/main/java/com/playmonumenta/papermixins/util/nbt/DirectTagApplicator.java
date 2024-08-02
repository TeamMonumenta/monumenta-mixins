package com.playmonumenta.papermixins.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class DirectTagApplicator implements TagApplicator {
    private final CompoundTag tag;

    public DirectTagApplicator(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void put(String key, Tag tag) {
        this.tag.put(key, tag);
    }
}
