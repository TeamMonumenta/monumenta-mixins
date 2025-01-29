package com.playmonumenta.papermixins.util;

@FunctionalInterface
public interface RAII extends AutoCloseable {
	@Override
	void close();
}
