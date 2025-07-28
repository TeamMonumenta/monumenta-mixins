package com.playmonumenta.mixinapi.v1;

import com.playmonumenta.mixinapi.v1.item.CustomItemRegistry;
import com.playmonumenta.mixinapi.v1.item.CustomItems;
import com.playmonumenta.mixinapi.v1.resource.DataLoaderRegistry;
import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import net.fabricmc.loader.api.SemanticVersion;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("deprecation")
public interface MonumentaPaperAPI {
	static MonumentaPaperAPI getInstance() {
		return ImplLoader.INSTANCE;
	}

	EntityDamageEvent.DamageModifier getIframes();

	SemanticVersion getVersion();

	int getFreeMapId();

	@ApiStatus.Internal
	DataLoaderRegistry getDataLoaderRegistryAPI();

	@ApiStatus.Internal
	CustomItemRegistry getCustomItemRegistryAPI();

	@ApiStatus.Internal
	CustomItems getCustomItemsAPI();

	@ApiStatus.Internal
	RedisSyncIO getRedisSyncIO();

	@ApiStatus.Internal
	DataFix getDataFix();

	@ApiStatus.Internal
	HookAPI getHookAPI();
}
