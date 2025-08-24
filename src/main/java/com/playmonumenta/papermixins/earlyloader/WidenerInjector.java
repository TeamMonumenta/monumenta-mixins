package com.playmonumenta.papermixins.earlyloader;

import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.lib.accesswidener.AccessWidener;
import net.fabricmc.loader.impl.lib.accesswidener.AccessWidenerReader;

public class WidenerInjector {
    private final AccessWidener widener;

    public WidenerInjector(FabricLoader loader) {
        widener = ((FabricLoaderImpl) loader).getAccessWidener();
    }

    public void readFrom(Path pluginPath, FileSystem fs, String path) {
        final var awPath = fs.getPath(path);
        final var awReader = new AccessWidenerReader(widener);

        if (!Files.isRegularFile(awPath)) {
            throw new RuntimeException("Access widener '%s' not found in %s".formatted(path, pluginPath));
        }

        try (BufferedReader reader = Files.newBufferedReader(awPath)) {
            awReader.read(reader, FabricLauncherBase.getLauncher().getTargetNamespace());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read widener '%s' from %s".formatted(path, pluginPath), e);
        }
    }
}
