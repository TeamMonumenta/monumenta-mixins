package com.playmonumenta.papermixins.mcfunction.parse;

import com.playmonumenta.papermixins.ConfigManager;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class Diagnostics {
	public enum Level {
		WARN,
		ERROR
	}

	public record Entry(Level level, int line, String message) {

	}

	private final List<Entry> diagnostics = new ArrayList<>();
	private boolean hasError;

	private void report(Level level, int line, String format, Object... args) {
		diagnostics.add(new Entry(level, line, String.format(format, args)));
		if (level == Level.ERROR) {
			hasError = true;
		}
	}

	public void reportErr(int line, String format, Object... args) {
		report(Level.ERROR, line, format, args);
	}

	public void reportWarn(int line, String format, Object... args) {
		report(Level.WARN, line, format, args);
	}

	public boolean hasError() {
		return hasError;
	}

	public void dumpErrors(Logger logger, String pack, ResourceLocation id, List<String> lines) {
		int context = ConfigManager.getConfig().mcFunction.diagnosticContext;
		StringBuilder builder = new StringBuilder();

		if (diagnostics.isEmpty()) {
			return;
		}

		builder.append("While parsing function '").append(id).append("' from pack '").append(pack).append("'\n");

		for (final var diagnostic : diagnostics) {
			if (diagnostic.level() == Level.ERROR) {
				hasError = true;
			}

			builder.append(diagnostic.level())
				.append(" (")
				.append(id)
				.append(":")
				.append(diagnostic.line() + 1)
				.append("): ")
				.append(diagnostic.message())
				.append("\n");

			List<IntObjectPair<String>> lineEntry = new ArrayList<>();
			for (int i = diagnostic.line() - context; i <= diagnostic.line() + context; i++) {
				if (i >= 0 && i < lines.size()) {
					lineEntry.add(IntObjectPair.of(i + 1, lines.get(i)));
				}
			}

			final var pad = Integer.toString(lineEntry.get(lineEntry.size() - 1).firstInt()).length();
			for (var entry : lineEntry) {
				boolean isErrLine = entry.firstInt() == diagnostic.line() + 1;
				builder.append(String.format("%-" + pad + "d", entry.firstInt()))
					.append(isErrLine ? " * " : " | ")
					.append(entry.value());

				if (isErrLine) {
					builder.append(" <- HERE");
				}

				builder.append("\n");
			}
		}

		logger.warn(builder.toString());
	}
}
