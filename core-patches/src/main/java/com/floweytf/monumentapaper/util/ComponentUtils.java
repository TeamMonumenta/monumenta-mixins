package com.floweytf.monumentapaper.util;

import net.minecraft.network.chat.Component;

public class ComponentUtils {
    public static Component fLiteral(String format, Object... args) {
        return Component.literal(String.format(format, args));
    }
}
