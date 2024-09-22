package com.playmonumenta.papermixins.duck;

import net.minecraft.world.level.BaseSpawner;

public interface EntityAccess {
	BaseSpawner monumenta$getSpawner();

	void monumenta$setSpawner(BaseSpawner spawner);

	boolean monumenta$getDelveReprime();

	void monumenta$setDelveReprime(boolean delveReprime);
}
