package com.playmonumenta.papermixins.paperapi.v1;

import com.playmonumenta.mixinapi.v1.MonumentaPaperAPI;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The Hook API allows attaching arbitrary objects
 *
 * @author Floweynt
 * @since 2.0.0
 */
@ApiStatus.NonExtendable
public interface HookAPI {
	/**
	 * Represents an entity hook that can be persistent.
	 *
	 * @author Floweynt
	 * @since 2.0.0
	 */
	@ApiStatus.OverrideOnly
	interface Persistent {
		/**
		 * Persist the state of the attached state to the hook target.
		 *
		 * @param nbt The tag to save to.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		void save(ReadWriteNBT nbt);

		/**
		 * Load the state of the hook from NBT.
		 *
		 * @param nbt The tag to read from.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		void load(ReadableNBT nbt);
	}

	/**
	 * Obtains an instance of the API.
	 *
	 * @return The API.
	 * @author Floweynt
	 * @since 2.0.0
	 */
	@NotNull
	static HookAPI getInstance() {
		return MonumentaPaperAPI.getInstance().getHookAPI();
	}

	/**
	 * A handle that can be used to retrieve hooked data from an entity.
	 * @param <T> The type of the hook.
	 *
	 * @author Floweynt
	 * @since 2.0.0
	 */
	@ApiStatus.NonExtendable
	interface EntityHook<T> {
		/**
		 * Retrieves the instance of the hook from an entity.
		 * There is no persistence across entity loads/saves unless {@link HookAPI#definePersistentEntityHook(Supplier, NamespacedKey)}
		 * is used to define this hook.
		 *
		 * @param entity The entity to load from.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		T get(Entity entity);
	}

	@ApiStatus.Internal
	<T> EntityHook<T> doDefineEntityHook(Supplier<T> defaultSupplier);

	@ApiStatus.Internal
	<T extends Persistent> EntityHook<T> doDefinePersistentEntityHook(Supplier<T> defaultSupplier, NamespacedKey id);

	static <T> EntityHook<T> defineEntityHook(Supplier<T> defaultSupplier) {
		return getInstance().doDefineEntityHook(defaultSupplier);
	}

	static <T extends Persistent> EntityHook<T> definePersistentEntityHook(Supplier<T> defaultSupplier, NamespacedKey id) {
		return getInstance().doDefinePersistentEntityHook(defaultSupplier, id);
	}
}
