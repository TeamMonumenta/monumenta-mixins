package com.playmonumenta.mixinapi.v1;

import java.lang.reflect.InvocationTargetException;

class ImplLoader {
	final static MonumentaPaperAPI INSTANCE;

	static {
		try {
			INSTANCE = (MonumentaPaperAPI) Class.forName("com.playmonumenta.papermixins.impl.v1.MonumentaPaperAPIImpl")
				.getMethod("getInstance")
				.invoke(null);

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
				ClassNotFoundException e) {
			throw new RuntimeException("failed loading API", e);
		}
	}
}
