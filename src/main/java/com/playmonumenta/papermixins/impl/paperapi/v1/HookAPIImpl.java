package com.playmonumenta.papermixins.impl.paperapi.v1;

import com.playmonumenta.papermixins.duck.hook.EntityHookAccess;
import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;

public class HookAPIImpl implements HookAPI {
	private static final HookAPIImpl INSTANCE = new HookAPIImpl();

	public static HookAPIImpl getInstance() {
		return INSTANCE;
	}

	private final AtomicInteger idCounter = new AtomicInteger();

	@Override
	public <T> EntityHook<T> doDefineEntityHook(Supplier<T> defaultSupplier) {
		final var currentId = idCounter.getAndIncrement();

		return entity -> ((EntityHookAccess) ((CraftEntity) entity).getHandle())
			.monumenta$hook$simpleImpl(currentId, defaultSupplier);
	}

	@Override
	public <T extends Persistent> EntityHook<T> doDefinePersistentEntityHook(Supplier<T> defaultSupplier, NamespacedKey id) {
		final var currentId = idCounter.getAndIncrement();

		return entity -> ((EntityHookAccess) ((CraftEntity) entity).getHandle())
			.monumenta$hook$persistentImpl(currentId, id, defaultSupplier);
	}
}
