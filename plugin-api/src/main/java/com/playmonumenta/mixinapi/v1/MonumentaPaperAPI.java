package com.playmonumenta.mixinapi.v1;

import com.playmonumenta.mixinapi.v1.item.CustomItemRegistry;
import com.playmonumenta.mixinapi.v1.item.CustomItems;
import com.playmonumenta.mixinapi.v1.resource.DataLoaderRegistry;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.ApiStatus;
import org.semver4j.Semver;

@SuppressWarnings("deprecation")
public interface MonumentaPaperAPI {
	static MonumentaPaperAPI getInstance() {
		return ImplLoader.INSTANCE;
	}

	EntityDamageEvent.DamageModifier getIframes();

	Semver getVersion();

	@ApiStatus.Internal
	DataLoaderRegistry getDataLoaderRegistryAPI();

	@ApiStatus.Internal
	CustomItemRegistry getCustomItemRegistryAPI();

	@ApiStatus.Internal
	CustomItems getCustomItemsAPI();

	@ApiStatus.Internal
	RedisSyncIO getRedisSyncIO();
}
