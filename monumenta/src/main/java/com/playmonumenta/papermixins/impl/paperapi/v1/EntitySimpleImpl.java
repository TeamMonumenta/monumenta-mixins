package com.playmonumenta.papermixins.impl.paperapi.v1;

import com.playmonumenta.papermixins.duck.hook.EntityHookAccess;
import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import java.util.function.Supplier;
import org.bukkit.entity.Entity;

record EntitySimpleImpl<T>(int id, Supplier<T> supplier) implements HookAPI.EntityHook<T> {
	@Override
	@SuppressWarnings("unchecked")
	public T get(Entity entity) {
		return (T) EntityHookAccess.instance(entity).monumenta$getHooks().computeIfAbsent(id, n -> supplier.get());
	}

	@Override
	public boolean has(Entity entity) {
		return EntityHookAccess.instance(entity).monumenta$getHooks().containsKey(id);
	}

	@Override
	public void delete(Entity entity) {
		EntityHookAccess.instance(entity).monumenta$getHooks().remove(id);
	}
}
