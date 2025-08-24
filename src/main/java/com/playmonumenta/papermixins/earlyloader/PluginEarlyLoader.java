package com.playmonumenta.papermixins.earlyloader;

import com.playmonumenta.papermixins.util.Util;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;

/**
 * A loader for plugin-bound metadata that may be useful for us to inject.
 */
public class PluginEarlyLoader {
	private final FabricLoader loader = FabricLoader.getInstance();
	private final WidenerInjector widenerInjector = new WidenerInjector(loader);
	private final Version version = loader.getModContainer("paper").orElseThrow().getMetadata().getVersion();

	private void processWideners(Path pluginPath, FileSystem fs, Map<String, List<String>> byVer) {
		byVer.forEach((predicate, wideners) -> {
			try {
				if (VersionPredicateParser.parse(predicate).test(version)) {
					for (final var widener : wideners) {
						widenerInjector.readFrom(pluginPath, fs, widener);
					}
				}
			} catch (VersionParsingException e) {
				Util.sneakyThrow(e);
			}
		});
	}

	private void loadFor(Path pluginPath) throws IOException {
		if (!Files.isRegularFile(pluginPath)) {
			return;
		}

		if (!pluginPath.toString().endsWith(".jar")) {
			return;
		}

		try (final var zfs = FileSystems.newFileSystem(pluginPath)) {
			final var cfgPath = zfs.getPath("monumenta.plugin.json");

			if (!Files.isRegularFile(cfgPath)) {
				return;
			}

			final var parseResult = PluginMetadata.readPluginMetadata(pluginPath, cfgPath);

			if (parseResult.isEmpty()) {
				return;
			}

			final var metadata = parseResult.get();
			final var wideners = metadata.wideners();

			if (wideners != null) {
				processWideners(pluginPath, zfs, wideners);
			}
		}
	}

	public void doLoad() {
		final var pluginsDir = FabricLoader.getInstance().getGameDir().resolve("plugins");

		if (!Files.isDirectory(pluginsDir)) {
			return;
		}

		try (var stream = Files.list(pluginsDir)) {
			for (final var file : (Iterable<Path>) stream::iterator) {
				loadFor(file);
			}
		} catch (IOException e) {
			Util.sneakyThrow(e);
		}
	}
}
