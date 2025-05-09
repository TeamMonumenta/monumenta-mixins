package com.playmonumenta.papermixins.impl.paperapi.v1;

import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;

public class HookAPIImpl implements HookAPI {
	private static final HookAPIImpl INSTANCE = new HookAPIImpl();

	public static HookAPIImpl getInstance() {
		return INSTANCE;
	}

	private final AtomicInteger idCounter = new AtomicInteger();

	@Override
	public <T> EntityHook<T> doDefineEntityHook(Supplier<T> defaultSupplier) {
		return new EntitySimpleImpl<>(idCounter.getAndIncrement(), defaultSupplier);
	}

	@Override
	public <T extends Persistent> EntityHook<T> doDefinePersistentEntityHook(Supplier<T> factory, NamespacedKey key) {
		return new EntityPersistentImpl<>(idCounter.getAndIncrement(), factory, key);
	}
}
