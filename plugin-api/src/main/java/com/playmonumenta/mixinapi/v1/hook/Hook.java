package com.playmonumenta.mixinapi.v1.hook;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * The "handle" of a hook, use to access the hook.
 *
 * @param <T>
 * @param <A>
 */
@ApiStatus.NonExtendable
public interface Hook<T, A> {
	/**
	 * Gets the hook from the attachment point.
	 *
	 * @param attachment The attachment point (entity/blockentity) to obtain data from.
	 * @return The hook instance, or null if not present.
	 */
	@Nullable
	T get(A attachment);

	void set(A attachment, T hook);

	void remove(A attachment);

	default Optional<T> getOptional(A attachment) {
		return Optional.ofNullable(get(attachment));
	}

	default boolean has(A attachment) {
		return get(attachment) != null;
	}
}
