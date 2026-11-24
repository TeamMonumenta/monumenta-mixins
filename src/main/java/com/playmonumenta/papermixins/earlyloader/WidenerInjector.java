package com.playmonumenta.papermixins.earlyloader;

import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.lib.classtweaker.api.ClassTweaker;
import net.fabricmc.loader.impl.lib.classtweaker.api.ClassTweakerReader;

public class WidenerInjector {
	private final ClassTweaker widener;

	public WidenerInjector(FabricLoader loader) {
		widener = ((FabricLoaderImpl) loader).getClassTweaker();
	}

	public void readFrom(Path pluginPath, FileSystem fs, String path) {
		final var awPath = fs.getPath(path);
		final var awReader = ClassTweakerReader.create(widener);

		if (!Files.isRegularFile(awPath)) {
			throw new RuntimeException("Access widener '%s' not found in %s".formatted(path, pluginPath));
		}

		try (BufferedReader reader = Files.newBufferedReader(awPath)) {
			awReader.read(reader, FabricLauncherBase.getLauncher().getMappingConfiguration().getRuntimeNamespace());
		} catch (Exception e) {
			throw new RuntimeException("Failed to read widener '%s' from %s".formatted(path, pluginPath), e);
		}
	}
}
