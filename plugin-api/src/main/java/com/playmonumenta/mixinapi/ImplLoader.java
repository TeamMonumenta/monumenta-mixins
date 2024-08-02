package com.playmonumenta.mixinapi;

import java.lang.reflect.InvocationTargetException;

class ImplLoader {
    final static MonumentaPaperAPI INSTANCE;

    static {
        try {
            INSTANCE = (MonumentaPaperAPI) Class.forName("com.playmonumenta.papermixins.impl.MonumentaPaperAPIImpl")
                .getMethod("getInstance")
                .invoke(null);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException("failed loading API", e);
        }
    }
}
