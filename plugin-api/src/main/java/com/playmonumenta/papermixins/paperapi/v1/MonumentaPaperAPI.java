package com.playmonumenta.papermixins.paperapi.v1;

import com.playmonumenta.papermixins.paperapi.v1.resource.DataLoaderRegistry;
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

	@ApiStatus.Internal
	DataLoaderRegistry getDataLoaderRegistryAPI();

	@ApiStatus.Internal
	RedisSyncIO getRedisSyncIO();

	@ApiStatus.Internal
	DataFix getDataFix();
}
