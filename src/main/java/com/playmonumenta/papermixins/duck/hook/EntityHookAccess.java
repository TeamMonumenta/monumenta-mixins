package com.playmonumenta.papermixins.duck.hook;

import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;

public interface EntityHookAccess {
	<T> T monumenta$hook$simpleImpl(int id, Supplier<T> supplier);
	<T extends HookAPI.Persistent> T monumenta$hook$persistentImpl(int id, NamespacedKey key, Supplier<T> supplier);
}
