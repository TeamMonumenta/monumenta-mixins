package com.playmonumenta.papermixins.impl.v1.resource;

import com.google.common.base.Preconditions;
import com.playmonumenta.mixinapi.v1.resource.DataLoaderRegistry;
import com.playmonumenta.mixinapi.v1.resource.DatapackResourceLoader;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DataLoaderRegistryImpl implements DataLoaderRegistry {
	private static final DataLoaderRegistryImpl INSTANCE = new DataLoaderRegistryImpl();
	private final List<Pair<String, DatapackResourceLoader>> loaders = new ArrayList<>();

	private DataLoaderRegistryImpl() {

	}

	public static DataLoaderRegistryImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public void addDatapackLoader(@NotNull String prefix, @NotNull DatapackResourceLoader loader) {
		Preconditions.checkNotNull(prefix);
		Preconditions.checkNotNull(loader);

		if (prefix.matches("a-z0-9/\\._-")) {
			throw new IllegalArgumentException("bad path prefix fragment when registering datapack loader");
		}

		if (prefix.endsWith("/")) {
			throw new IllegalArgumentException("path prefix must not end with /");
		}

		loaders.add(Pair.of(prefix, loader));
	}

	public List<Pair<String, DatapackResourceLoader>> getLoaders() {
		return Collections.unmodifiableList(loaders);
	}
}
