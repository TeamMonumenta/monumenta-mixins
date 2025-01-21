package com.playmonumenta.papermixins.impl.paperapi.v1;

import com.playmonumenta.papermixins.duck.hook.EntityHookAccess;
import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import de.tr7zw.nbtapi.NBTContainer;
import it.unimi.dsi.fastutil.Pair;
import java.util.Optional;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

record EntityPersistentImpl<T extends HookAPI.Persistent>(
	int id, Supplier<T> supplier, NamespacedKey key
) implements HookAPI.EntityHook<T> {
	@SuppressWarnings("unchecked")
	private Optional<T> tryLoad(EntityHookAccess access) {
		if (access.monumenta$getHooks().containsKey(id)) {
			return Optional.of((T) access.monumenta$getHooks().get(id));
		}

		if (!access.monumenta$getHookPersistentData().containsKey(key)) {
			return Optional.empty();
		}

		final var instance = supplier.get();
		instance.load(new NBTContainer(access.monumenta$getHookPersistentData().get(key)));
		access.monumenta$getHookPersistentData().remove(key);
		access.monumenta$getPersistentEntries().add(Pair.of(key, instance));
		access.monumenta$getHooks().put(id, instance);
		return Optional.of(instance);
	}

	@Override
	public T get(Entity entity) {
		final var access = EntityHookAccess.instance(entity);
		return tryLoad(access).orElseGet(() -> {
			final var instance = supplier.get();
			access.monumenta$getHooks().put(id, instance);
			return instance;
		});
	}

	@Override
	public boolean has(Entity entity) {
		return tryLoad(EntityHookAccess.instance(entity)).isPresent();
	}

	@Override
	public void delete(Entity entity) {
		final var access = EntityHookAccess.instance(entity);
		access.monumenta$getHooks().remove(id);
		access.monumenta$getHookPersistentData().remove(key);
		access.monumenta$getPersistentEntries().removeIf(x -> x.first().equals(key));
	}
}
