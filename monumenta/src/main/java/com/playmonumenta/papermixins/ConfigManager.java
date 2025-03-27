package com.playmonumenta.papermixins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class ConfigManager {
	private static final Logger LOGGER = MonumentaMod.getLogger("Config");

	private static final Path CONFIG_PATH = Path.of("config/monumenta-mixins.yml");
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
}
