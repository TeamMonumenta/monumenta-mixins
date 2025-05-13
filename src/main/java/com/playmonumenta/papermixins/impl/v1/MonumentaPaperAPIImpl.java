package com.playmonumenta.papermixins.impl.v1;

import com.playmonumenta.mixinapi.v1.DataFix;
import com.playmonumenta.mixinapi.v1.MonumentaPaperAPI;
import com.playmonumenta.mixinapi.v1.RedisSyncIO;
import com.playmonumenta.mixinapi.v1.hook.HookAPI;
import com.playmonumenta.mixinapi.v1.item.CustomItemRegistry;
import com.playmonumenta.mixinapi.v1.item.CustomItems;
import com.playmonumenta.mixinapi.v1.resource.DataLoaderRegistry;
import com.playmonumenta.papermixins.VersionInfo;
import com.playmonumenta.papermixins.impl.v1.hook.HookAPIImpl;
import net.fabricmc.loader.api.SemanticVersion;
import org.bukkit.event.entity.EntityDamageEvent;

@SuppressWarnings("deprecation")
public class MonumentaPaperAPIImpl implements MonumentaPaperAPI {
	private static MonumentaPaperAPIImpl INSTANCE = null;
	private final EntityDamageEvent.DamageModifier iframes = EntityDamageEvent.DamageModifier.valueOf("IFRAMES");

	public static MonumentaPaperAPIImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MonumentaPaperAPIImpl();
		}

		return INSTANCE;
	}

	@Override
	public EntityDamageEvent.DamageModifier getIframes() {
		return iframes;
	}

	@Override
	public SemanticVersion getVersion() {
		return VersionInfo.VERSION;
	}

	@Override
	public DataLoaderRegistry getDataLoaderRegistryAPI() {
		return DataLoaderRegistry.getInstance();
	}

	@Override
	public CustomItemRegistry getCustomItemRegistryAPI() {
		return CustomItemRegistry.getInstance();
	}

	@Override
	public CustomItems getCustomItemsAPI() {
		return CustomItems.getInstance();
	}

	@Override
	public RedisSyncIO getRedisSyncIO() {
		return RedisSyncIOImpl.getInstance();
	}

	@Override
	public DataFix getDataFix() {
		return DataFixImpl.getInstance();
	}

	public HookAPI getHookAPI() {
		return HookAPIImpl.getInstance();
	}
}
