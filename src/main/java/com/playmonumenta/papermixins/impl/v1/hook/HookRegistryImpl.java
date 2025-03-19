package com.playmonumenta.papermixins.impl.v1.hook;

import com.google.common.base.Preconditions;
import com.playmonumenta.mixinapi.v1.hook.Hook;
import com.playmonumenta.mixinapi.v1.hook.HookRegistry;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

public class HookRegistryImpl<T> implements HookRegistry<T> {
	public static boolean isReady = false;

	private final Map<NamespacedKey, HookImpl<?, T>> map = new HashMap<>();

	@Override
	public void register(NamespacedKey key, Hook<?, T> hook) {
		Preconditions.checkArgument(hook instanceof HookImpl<?, T>, "don't subclass Hook");
		Preconditions.checkState(isReady, "not ready for registration (registered too late?)");
		Preconditions.checkArgument(!map.containsKey(key), "duplicate key " + key);
		map.put(key, (HookImpl<?, T>) hook);
	}

	@Override
	public @Nullable HookImpl<?, T> get(NamespacedKey key) {
		return map.get(key);
	}
}
