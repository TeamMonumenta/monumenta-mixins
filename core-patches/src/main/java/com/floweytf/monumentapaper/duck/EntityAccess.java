package com.floweytf.monumentapaper.duck;

import net.minecraft.world.level.BaseSpawner;

public interface EntityAccess {
    BaseSpawner getSpawner();

    void setSpawner(BaseSpawner spawner);

    boolean getDelveReprime();

    void setDelveReprime(boolean delveReprime);
}
