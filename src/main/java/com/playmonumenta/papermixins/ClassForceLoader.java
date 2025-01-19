package com.playmonumenta.papermixins;

import com.playmonumenta.papermixins.mixin.accessor.JavaPluginAccessor;
import com.playmonumenta.papermixins.util.Util;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.fabricmc.loader.api.ModContainer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class ClassForceLoader {
	private static final Logger LOGGER = MonumentaMod.getLogger("ClassForceLoader");
	private static final String MIXIN_PACKAGE = "com.playmonumenta.papermixins.mixin";

	public static class StopException extends RuntimeException {
	}

	private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
		final var thread = new Thread(runnable);
		thread.setName("ClassForceLoadWorker");
		return thread;
	});
	private final AtomicBoolean flag = new AtomicBoolean();
	private int classesLoaded;

	private void forceLoadSingle(ClassLoader classLoader, Path path, Predicate<String> validate) throws IOException {
		try (final var entry = Files.walk(path)) {
			entry.forEach(l -> {
				if (flag.get()) {
					throw new StopException();
				}

				final var frag = path.relativize(l).toString();

				if (!frag.endsWith(".class")) {
					return;
				}

				final var clazz = frag.substring(0, frag.length() - ".class".length()).replace('/', '.');

				if (!validate.test(clazz)) {
					return;
				}

				try {
					classLoader.loadClass(clazz);
					classesLoaded++;
				} catch (Throwable ignored) {
					LOGGER.info("Failed to load class {}", clazz);
				}
			});
		}
	}

	private void forceLoad0(ClassLoader classLoader, List<Path> paths, Predicate<String> validate, String source) {
		final var originalClassesLoaded = classesLoaded;
		final var ms = Util.profile(() -> {
			try {
				for (var path : paths) {
					if (flag.get()) {
						return;
					}

					if (path.toString().endsWith(".jar")) {
						try (final var fs = FileSystems.newFileSystem(path)) {
							forceLoadSingle(classLoader, fs.getPath("/"), validate);
						}
					} else {
						forceLoadSingle(classLoader, path, validate);
					}
				}
			} catch (IOException e) {
				Util.sneakyThrow(e);
			} catch (StopException ignored) {
			}
		});

		final var newClassesLoaded = classesLoaded - originalClassesLoaded;

		LOGGER.info("Force-loaded {} classes in {}ms from {}", newClassesLoaded, ms, source);
	}

	public void forceLoadSelf() {
		forceLoadMod(VersionInfo.MOD);
	}

	public void forceLoadMod(ModContainer container) {
		forceLoad(
			getClass().getClassLoader(),
			container.getRootPaths(),
			x -> !x.startsWith(MIXIN_PACKAGE), "Mod[" + container.getMetadata().getId() + "]"
		);
	}

	public void forceLoadPlugin(JavaPlugin plugin, Predicate<String> predicate) {
		final var access = Util.<JavaPluginAccessor>c(plugin);
		forceLoad(
			access.invokeGetClassLoader(),
			List.of(access.invokeGetFile().toPath()),
			predicate,
			"Plugin[" + plugin.getName() + "]"
		);
	}

	public void forceLoad(ClassLoader loader, List<Path> targets, Predicate<String> validate, String source) {
		executor.submit(() -> forceLoad0(loader, targets, validate, source));
	}

	public void stop() {
		flag.set(true);
		executor.shutdownNow();
	}

	private Predicate<String> createFilter(List<String> entries) {
		final var parsed = new String[entries.size()];
		final var type = new boolean[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			final var j = entries.size() - i - 1;
			final var entry = entries.get(i);
			if (entry.startsWith("-")) {
				type[j] = false;
				parsed[j] = entry.substring(1);
			} else {
				type[j] = true;
				parsed[j] = entry;
			}
		}

		return c -> {
			for (int i = 0; i < parsed.length; i++) {
				if (c.startsWith(parsed[i])) {
					return type[i];
				}
			}

			return false;
		};
	}

	public void init() {
		final var config = ConfigManager.getConfig();
		final var pluginManager = Bukkit.getPluginManager();

		if (config.classLoading.forceLoadSelfClasses) {
			forceLoadSelf();
		}

		for (final var forceLoadEntry : config.classLoading.plugins) {
			final var plugin = pluginManager.getPlugin(forceLoadEntry.pluginName);
			if (plugin == null) {
				LOGGER.warn("Unknown plugin {}", forceLoadEntry.pluginName);
				continue;
			}

			if (!(plugin instanceof JavaPlugin javaPlugin)) {
				LOGGER.warn("Illegal plugin {}", forceLoadEntry.pluginName);
				continue;
			}

			forceLoadPlugin(javaPlugin, createFilter(forceLoadEntry.filters));
		}
	}
}
