package com.playmonumenta.papermixins.mixin.behavior.spawner;

import com.playmonumenta.papermixins.duck.EntityAccess;
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
	public BaseSpawner monumenta$getSpawner() {
		return monumenta$spawnerSpawnedBy;
	}

	@Override
	public void monumenta$setSpawner(BaseSpawner spawner) {
		monumenta$spawnerSpawnedBy = spawner;
	}

	@Override
	public boolean monumenta$getDelveReprime() {
		return monumenta$delveReprime;
	}

	@Override
	public void monumenta$setDelveReprime(boolean delveReprime) {
		this.monumenta$delveReprime = delveReprime;
	}
}
