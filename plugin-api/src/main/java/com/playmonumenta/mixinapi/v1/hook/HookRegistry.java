package com.playmonumenta.mixinapi.v1.hook;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface HookRegistry<T> {
	void register(NamespacedKey key, Hook<?, T> hook);

	@Nullable
	Hook<?, T> get(NamespacedKey key);
}
