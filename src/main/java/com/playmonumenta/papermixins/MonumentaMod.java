package com.playmonumenta.papermixins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class MonumentaMod {
	public static final Logger LOGGER = getLogger("");

	// we create this dummy file object that never exists
	// this tricks MC into not reading from file
	public static final File FAKE_FILE = new File("") {
		@Override
		public boolean exists() {
			return false;
		}
	};

	// State management
	public static final ThreadLocal<Function<? super Double, Double>> IFRAME_FUNC = new ThreadLocal<>();
	public static final ThreadLocal<Double> IFRAME_VALUE = new ThreadLocal<>();
	private static final Path CONFIG_PATH = Path.of("config/monumenta-mixins.yml");
	public static boolean HAS_PLUGINS = true;
	private static final YamlConfigurationLoader CONFIG_LOADER = YamlConfigurationLoader.builder()
		.source(() -> Files.newBufferedReader(CONFIG_PATH))
		.sink(() -> Files.newBufferedWriter(CONFIG_PATH))
		.nodeStyle(NodeStyle.BLOCK)
		.indent(4)
		.build();
	// config
	private static Config config;

	public static Config getConfig() {
		if (config == null) {
			loadConfig();
		}

		return config;
	}

	private static void loadConfig() {
		LOGGER.info("Loading configuration from {}", CONFIG_PATH);
		try {
			Files.createDirectories(Path.of("config"));

			if (!Files.exists(CONFIG_PATH)) {
				config = new Config();
				LOGGER.info("Config not found - generating defaults");
				CONFIG_LOADER.save(CONFIG_LOADER.createNode().set(config));
				return;
			}

			config = CONFIG_LOADER.load().get(Config.class);
		} catch (IOException e) {
			throw new RuntimeException("failed to load config", e);
		}
	}

	// random stuff

	public static String getIdentifier() {
		return String.format("MonumentaPaper (%s) %s", VersionInfo.MOD_ID, VersionInfo.VERSION);
	}

	public static Logger getLogger(String subsystem) {
		return LogManager.getLogger(VersionInfo.IDENTIFIER + "/" + subsystem);
	}
}
