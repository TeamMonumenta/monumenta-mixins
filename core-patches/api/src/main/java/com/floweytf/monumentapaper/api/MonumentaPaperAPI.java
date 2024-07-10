package com.floweytf.monumentapaper.api;

import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
public class MonumentaPaperAPI {
    private static final Class<?> MONUMENTA_CLASS;
    private static final Method GET_IDENTIFIER_METHOD;

    static {
        try {
            MONUMENTA_CLASS = Class.forName("com.floweytf.monumentapaper.Monumenta");
            VERSION = (String) MONUMENTA_CLASS.getField("VER_VERSION").get(null);
            GET_IDENTIFIER_METHOD = MONUMENTA_CLASS.getMethod("getIdentifier");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Don't worry about how this enumerator has an instance without patching API jar...
     */
    public static final EntityDamageEvent.DamageModifier IFRAMES = EntityDamageEvent.DamageModifier.valueOf("IFRAMES");
    public static final String VERSION;

    private static int flyingTickTime = 120;
    private static int serverShutdownTime = 20000;

    public static int getFlyingTickTime() {
        return flyingTickTime;
    }

    // actually useful API functions
    public static void setFlyingTickTime(int flyingTickTime) {
        MonumentaPaperAPI.flyingTickTime = Math.max(0, flyingTickTime);
    }

    public static int getServerShutdownTime() {
        return serverShutdownTime;
    }

    public static void setServerShutdownTime(int flyingTickTime) {
        MonumentaPaperAPI.serverShutdownTime = Math.max(1000, flyingTickTime);
    }

    public static String getIdentifier() {
        try {
            return (String) GET_IDENTIFIER_METHOD.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
