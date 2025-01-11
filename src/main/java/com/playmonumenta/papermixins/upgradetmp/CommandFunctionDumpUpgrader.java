package com.playmonumenta.papermixins.upgradetmp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandFunctionDumpUpgrader {
	private static void process(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path);
		List<String> outputLines = new ArrayList<>();
		for (String line : lines) {
			line = process(line);
			outputLines.add(line);
		}
		Files.write(path, outputLines);
	}

	public static String process(String string) {
		if (string.startsWith("#")) {
			return string;
		}
		int i = 0;
		for (char meow : string.toCharArray()) {
			if (Character.isWhitespace(meow)) {
				i++;
			} else {
				break;
			}
		}
		string = string.substring(0, i) + SimpleCommandUpgrader.updateSingle(string);
		return string;
	}

	public static void doUpdate() {
		try {
			Files.walk(Path.of("tmp-upgrade/datapacks")).forEach(path -> {
				if (!Files.isRegularFile(path)) {
					return;
				}

				if (!path.toString().endsWith(".mcfunction")) {
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
