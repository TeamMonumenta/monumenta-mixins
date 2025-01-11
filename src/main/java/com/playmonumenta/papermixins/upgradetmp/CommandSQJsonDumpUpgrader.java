package com.playmonumenta.papermixins.upgradetmp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandSQJsonDumpUpgrader {
	private static final Logger LOGGER = LoggerFactory.getLogger("CommandSQJsonDumpUpgrader");
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	private static void process(Path path) throws IOException {
		JsonElement data;
		try (final var reader = Files.newBufferedReader(path)) {
			data = GSON.fromJson(reader, JsonElement.class);
			process(data);
		}
		try (final var writer = Files.newBufferedWriter(path)) {
			GSON.toJson(data, writer);
		}
	}

	private static void process(JsonElement element) {
		if (element == null) {
			return;
		}
		if (element instanceof JsonObject object) {
			if (object.has("command")) {
				final var command = object.get("command").getAsString();
				final var newCommand = SimpleCommandUpgrader.updateSingle(command);
				object.addProperty("command", newCommand);
			}

			for (final Map.Entry<String,JsonElement> entry : object.entrySet()) {
				final var jsonElement = entry.getValue();
				process(jsonElement);
			}
		} else if (element instanceof JsonArray array) {
			for (final var jsonElement : array) {
				process(jsonElement);
			}
		}
	}

	public static void doUpdate() {
		try {
			Files.walk(Path.of("tmp-upgrade/scriptedquests")).forEach(path -> {
				if (!Files.isRegularFile(path)) {
					return;
				}

				if (!path.toString().endsWith(".json")) {
					return;
				}

				try {
					process(path);
				} catch (Exception e) {
					LOGGER.error("Failed to process file {}", path, e);
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
