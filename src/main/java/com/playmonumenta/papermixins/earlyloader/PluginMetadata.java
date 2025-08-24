package com.playmonumenta.papermixins.earlyloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.loader.impl.lib.gson.JsonReader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.jetbrains.annotations.Nullable;

record PluginMetadata(@Nullable Map<String, List<String>> wideners) {
    private static final LogCategory CATEGORY = LogCategory.createCustom("Monumenta", "Plugin", "ConfigParser");

    private static Map<String, List<String>> readWideners(JsonReader reader) throws IOException {
        reader.beginObject();

        final var widenersByVersion = new HashMap<String, List<String>>();

        while (reader.hasNext()) {
            final var name = reader.nextName();
            final var wideners = new ArrayList<String>();

            reader.beginArray();

            while (reader.hasNext()) {
                wideners.add(reader.nextString());
            }

            reader.endArray();

            widenersByVersion.put(name, wideners);
        }

        reader.endObject();

        return widenersByVersion;
    }

    public static Optional<PluginMetadata> readPluginMetadata(Path pluginPath, Path cfgPath) {
        try (final var reader = new JsonReader(Files.newBufferedReader(cfgPath))) {
            reader.beginObject();

            Map<String, List<String>> wideners = null;

            while (reader.hasNext()) {
                final var name = reader.nextName();

                if (name.equals("wideners")) {
                    wideners = readWideners(reader);
                }
            }

            reader.endObject();

            return Optional.of(new PluginMetadata(wideners));
        } catch (Exception e) {
            Log.error(CATEGORY, "failed to monumenta.plugin.json from plugin " + pluginPath, e);
        }

        return Optional.empty();
    }
}