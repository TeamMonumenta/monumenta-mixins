package com.playmonumenta.papermixins.impl.v1.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.playmonumenta.mixinapi.v1.resource.DatapackResourceManager;
import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.util.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PluginDataListener extends SimplePreparableReloadListener<Void> {
	public static final Logger LOGGER = MonumentaMod.getLogger("Datapack");

	public static final PluginDataListener INSTANCE = new PluginDataListener();
	private static final String PREFIX = "plugin";
	private static final String SUFFIX = ".json";
	public static boolean PLUGIN_INIT = false;
	private final Map<ResourceLocation, JsonElement> data = new HashMap<>();

	private PluginDataListener() {

	}

	@Override
	protected @NotNull Void prepare(@NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
		return null;
	}

	@Override
	protected void apply(@NotNull Void prepared, ResourceManager manager, @NotNull ProfilerFiller profiler) {
		data.clear();
		manager.listResources(PREFIX, x -> x.getPath().endsWith(SUFFIX))
			.forEach((k, v) -> {
				try {
					data.put(
						k.withPath(p -> p.substring(PREFIX.length() + 1, p.length() - SUFFIX.length())),
						new Gson().fromJson(v.openAsReader(), JsonElement.class)
					);
				} catch (Throwable e) {
					LOGGER.error("While loading entry {}: ", k, e);
				}
			});

		if (PLUGIN_INIT) {
			reload(false);
		}
	}

	public void reload(boolean isFirst) {
		long ms = Util.profile(() -> {
			for (final var loaderEntry : DataLoaderRegistryImpl.getInstance().getLoaders()) {
				final var loader = loaderEntry.second();
				final var loaderPrefix = loaderEntry.first();

				if (!(loader.canReload() || isFirst)) {
					continue;
				}

				final DatapackResourceManager manager = () -> data.entrySet().stream()
					.filter(entry -> entry.getKey().getPath().startsWith(loaderPrefix))
					.collect(Collectors.toUnmodifiableMap(
						entry -> CraftNamespacedKey.fromMinecraft(entry.getKey().withPath(path -> path.substring(loaderPrefix.length() + 1))),
						entry -> entry.getValue().getAsJsonObject()
					));

				try {
					loader.load(manager);
				} catch (Throwable e) {
					LOGGER.error("Datapack loading failed for {}", loaderPrefix, e);
				}
			}
		});

		LOGGER.info("Datapack loading took {}ms", ms);
	}
}
