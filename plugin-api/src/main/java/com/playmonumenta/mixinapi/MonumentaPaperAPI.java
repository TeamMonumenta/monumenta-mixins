package com.playmonumenta.mixinapi;

import com.playmonumenta.mixinapi.item.CustomItemRegistry;
import com.playmonumenta.mixinapi.item.CustomItems;
import com.playmonumenta.mixinapi.resource.DataLoaderRegistry;
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

	int getFlyingTickTime();

	void setFlyingTickTime(int time);

	int getServerShutdownTime();

	void setServerShutdownTime(int time);

	@ApiStatus.Internal
	DataLoaderRegistry getDataLoaderRegistryAPI();

	@ApiStatus.Internal
	CustomItemRegistry getCustomItemRegistryAPI();

	@ApiStatus.Internal
	CustomItems getCustomItemsAPI();
}
