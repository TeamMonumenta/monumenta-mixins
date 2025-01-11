package com.playmonumenta.papermixins.upgradetmp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandJsonDumpUpgrader {
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

	private static void process(JsonElement object) {
		if (object == null) {
			return;
		}
		for (final var jsonElement : object.getAsJsonArray()) {
			final var command = jsonElement.getAsJsonObject().get("command").getAsString();
			final var newCommand = SimpleCommandUpgrader.updateSingle(command);
			jsonElement.getAsJsonObject().addProperty("command", newCommand);
		}
	}

	public static void doUpdate() {
		try {
			Files.walk(Path.of("tmp-upgrade")).forEach(path -> {
				if (!Files.isRegularFile(path)) {
					return;
				}

				try {
					process(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
