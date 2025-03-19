package com.playmonumenta.papermixins.impl.v1.hook;

import com.playmonumenta.mixinapi.v1.hook.Hook;
import com.playmonumenta.mixinapi.v1.hook.HookAPI;
import com.playmonumenta.mixinapi.v1.hook.Persistent;
import com.playmonumenta.mixinapi.v1.hook.Ticker;
import com.playmonumenta.papermixins.MonumentaMod;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.slf4j.Logger;

public class HookAPIImpl implements HookAPI {
	private static final HookAPIImpl INSTANCE = new HookAPIImpl();
	public static final Logger LOGGER = MonumentaMod.getLogger("Hook");

	public static HookAPIImpl getInstance() {
		return INSTANCE;
	}

	private final HookRegistryImpl<Entity> entityRegistry = new HookRegistryImpl<>();
	private final HookRegistryImpl<TileState> blockEntityRegistry = new HookRegistryImpl<>();

	@Override
	public HookRegistryImpl<Entity> getEntityRegistry() {
		return entityRegistry;
	}

	@Override
	public HookRegistryImpl<TileState> getBlockEntityRegistry() {
		return blockEntityRegistry;
	}

	@SuppressWarnings("unchecked")
	private <T, A> Hook<T, A> doDefine(NamespacedKey key, Class<T> clazz) {
		try {
			return new HookImpl<T, A>(
				key,
				Ticker.class.isAssignableFrom(clazz),
				Persistent.class.isAssignableFrom(clazz) ?
					MethodHandleProxies.asInterfaceInstance(
						Supplier.class,
						MethodHandles.lookup().findConstructor(clazz, MethodType.methodType(void.class))
					) : null
			);
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException("persistent class must be default-constructible", e);
		}
	}
}
