package com.playmonumenta.papermixins.util;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Util {
	public static <T> @NotNull T coalesce(@Nullable T value, @NotNull Supplier<T> defaultSupplier) {
		if (value == null)
			return defaultSupplier.get();
		return value;
	}


	public static long profile(Runnable func) {
		long start = System.currentTimeMillis();
		func.run();
		long end = System.currentTimeMillis();
		return end - start;
	}

	@SuppressWarnings("unchecked")
	public static <T> @NotNull T c(@NotNull Object o) {
		return (T) o;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> RuntimeException sneakyThrow(Throwable ex) throws E {
		throw (E) ex;
	}
}
