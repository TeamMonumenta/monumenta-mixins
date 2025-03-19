package com.playmonumenta.papermixins.impl.v1.hook;

import com.playmonumenta.mixinapi.v1.hook.Hook;
import com.playmonumenta.papermixins.duck.HookHolderAccess;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public record HookImpl<T, A>(
	NamespacedKey id,
	boolean isTicking,
	Supplier<T> persistenceConstructor
) implements Hook<T, A> {
	@Override
	public @Nullable T get(A attachment) {
		return ((HookHolderAccess<A>) attachment).monumenta$getHookHolder().get(this);
	}

	@Override
	public void set(A attachment, T hook) {
		((HookHolderAccess<A>) attachment).monumenta$getHookHolder().set(this, hook);
	}

	@Override
	public void remove(A attachment) {
		((HookHolderAccess<A>) attachment).monumenta$getHookHolder().remove(this);
	}

	public boolean isPersistent() {
		return persistenceConstructor != null;
	}
}
