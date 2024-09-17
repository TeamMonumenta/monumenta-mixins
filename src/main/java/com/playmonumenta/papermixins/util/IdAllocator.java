package com.playmonumenta.papermixins.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdAllocator<T> {
    private final List<T> list = new ArrayList<>();
    private final Object2IntMap<T> map = new Object2IntOpenHashMap<>();

    public IdAllocator() {
    }

    public List<T> entries() {
        return Collections.unmodifiableList(list);
    }

    public int getOrAllocateId(T entry) {
        if (!map.containsKey(entry)) {
            final var id = list.size();
            list.add(entry);
            map.put(entry, id);
            return id;
        }

        return map.getInt(entry);
    }
}
