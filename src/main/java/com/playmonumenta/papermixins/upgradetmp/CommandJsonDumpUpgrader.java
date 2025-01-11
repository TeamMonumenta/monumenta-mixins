package com.playmonumenta.papermixins.upgradetmp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandJsonDumpUpgrader {
	private static final Gson GSON = new Gson();

	private static void process(Path path) throws IOException {
		final var data = GSON.fromJson(Files.newBufferedReader(path), JsonElement.class);
		process(data);
		GSON.toJson(path, Files.newBufferedWriter(path));
	}

	private static void process(JsonElement object) {
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
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
