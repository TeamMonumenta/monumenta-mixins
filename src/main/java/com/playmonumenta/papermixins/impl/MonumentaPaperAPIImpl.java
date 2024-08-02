package com.playmonumenta.papermixins.impl;

import com.playmonumenta.mixinapi.MonumentaPaperAPI;
import com.playmonumenta.mixinapi.item.CustomItemRegistry;
import com.playmonumenta.mixinapi.item.CustomItems;
import com.playmonumenta.mixinapi.resource.DataLoaderRegistry;
import com.playmonumenta.papermixins.VersionInfo;
import org.bukkit.event.entity.EntityDamageEvent;
import org.semver4j.Semver;

@SuppressWarnings("deprecation")
public class MonumentaPaperAPIImpl implements MonumentaPaperAPI {
    private static MonumentaPaperAPIImpl INSTANCE = null;

    public static MonumentaPaperAPIImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MonumentaPaperAPIImpl();
        }

        return INSTANCE;
    }

    private final EntityDamageEvent.DamageModifier iframes = EntityDamageEvent.DamageModifier.valueOf("IFRAMES");
    private int flyingTickTime;
    private int serverShutdownTime;

    @Override
    public EntityDamageEvent.DamageModifier getIframes() {
        return iframes;
    }

    @Override
    public Semver getVersion() {
        return VersionInfo.VERSION;
    }

    @Override
    public int getFlyingTickTime() {
        return flyingTickTime;
    }

    @Override
    public void setFlyingTickTime(int time) {
        flyingTickTime = Math.max(0, time);
    }

    @Override
    public int getServerShutdownTime() {
        return serverShutdownTime;
    }

    @Override
    public void setServerShutdownTime(int time) {
        serverShutdownTime = Math.max(1000, time);
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
    public DataLoaderRegistry getDataLoaderRegistryAPI() {
        return DataLoaderRegistry.getInstance();
    }
}
