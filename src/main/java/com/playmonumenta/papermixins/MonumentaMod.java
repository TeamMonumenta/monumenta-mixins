package com.playmonumenta.papermixins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonumentaMod {
	public static final Logger LOGGER = getLogger("");
	public static final ClassForceLoader FORCE_LOADER = new ClassForceLoader();

	public static String getIdentifier() {
		return String.format("MonumentaPaper (%s) %s", VersionInfo.MOD_ID, VersionInfo.VERSION);
	}

	public static Logger getLogger(String subsystem) {
		return LoggerFactory.getLogger(VersionInfo.IDENTIFIER + "/" + subsystem);
	}

	public static void onStart() {
		MonumentaMod.LOGGER.info("Running {}", MonumentaMod.getIdentifier());
	}

	public static void onStop() {
		FORCE_LOADER.stop();
	}

	public static void onPluginLoaded() {
		FORCE_LOADER.init();
	}
}
