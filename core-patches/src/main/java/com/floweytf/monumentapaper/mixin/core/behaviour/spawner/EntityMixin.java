package com.floweytf.monumentapaper.mixin.core.behaviour.spawner;

import com.floweytf.monumentapaper.duck.EntityAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Flowey
 * @mm-patch 0025-Monumenta-Mobs-that-despawn-return-to-their-spawners.patch
 * <p>
 * Mobs that despawn return to their spawners.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccess {

    @Unique
    public BaseSpawner monumenta$spawnerSpawnedBy = null;

    @Unique
    public boolean monumenta$delveReprime = false;

    @Override
    public BaseSpawner getSpawner() {
        return monumenta$spawnerSpawnedBy;
    }

    @Override
    public void setSpawner(BaseSpawner spawner) {
        monumenta$spawnerSpawnedBy = spawner;
    }

    @Override
    public boolean getDelveReprime() {
        return monumenta$delveReprime;
    }

    @Override
    public void setDelveReprime(boolean delveReprime) {
        this.monumenta$delveReprime = delveReprime;
    }
}