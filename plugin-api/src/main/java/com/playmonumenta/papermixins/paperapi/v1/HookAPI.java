package com.playmonumenta.papermixins.paperapi.v1;

import com.playmonumenta.mixinapi.v1.MonumentaPaperAPI;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The Hook API allows attaching arbitrary objects to entities or other targets.
 * This API provides mechanisms to define hooks that can be used to store and retrieve custom data on entities.
 * <p>
 * Hooks should typically not be dynamically defined, but instead should be assigned to static final variables
 * where they are used. This ensures better performance and avoids unnecessary redefinition of hooks.
 *
 * @author Floweynt
 * @since 2.0.0
 */
@ApiStatus.NonExtendable
public interface HookAPI {

	/**
	 * Represents an entity hook that can be persistent, meaning it can store state across server restarts.
	 * Provides methods for saving and loading the hook's data.
	 *
	 * @author Floweynt
	 * @since 2.0.0
	 */
	@ApiStatus.OverrideOnly
	interface Persistent {

		/**
		 * Saves the state of the hook to the provided NBT tag.
		 * This allows the hook's data to be persisted across server restarts.
		 *
		 * @param nbt The NBT tag to which the hook's state will be saved.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		void save(ReadWriteNBT nbt);

		/**
		 * Loads the state of the hook from the provided NBT tag.
		 * This allows the hook's data to be restored from a saved state.
		 *
		 * @param nbt The NBT tag from which the hook's state will be loaded.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		void load(ReadableNBT nbt);
	}

	/**
	 * Retrieves an instance of the HookAPI. This is the entry point to access hook functionality.
	 *
	 * @return The current instance of HookAPI.
	 * @author Floweynt
	 * @since 2.0.0
	 */
	@NotNull
	static HookAPI getInstance() {
		return MonumentaPaperAPI.getInstance().getHookAPI();
	}

	/**
	 * A handle for retrieving hooked data from an entity. This hook may store arbitrary data on an entity.
	 *
	 * @param <T> The type of the data that is stored in the hook.
	 * @author Floweynt
	 * @since 2.0.0
	 */
	@ApiStatus.NonExtendable
	interface EntityHook<T> {

		/**
		 * Retrieves the instance of the hook from the specified entity.
		 * The hook is not persistent by default unless explicitly defined as persistent.
		 *
		 * @param entity The entity from which to retrieve the hook.
		 * @return The hook instance associated with the entity.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		T get(Entity entity);

		/**
		 * Checks if the hook is present on the specified entity.
		 *
		 * @param entity The entity to check for the presence of the hook.
		 * @return true if the hook exists on the entity, false otherwise.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		boolean has(Entity entity);

		/**
		 * Deletes the hook from the specified entity, removing the associated data.
		 *
		 * @param entity The entity from which the hook will be removed.
		 * @author Floweynt
		 * @since 2.0.0
		 */
		void delete(Entity entity);
	}

	@ApiStatus.Internal
	<T> EntityHook<T> doDefineEntityHook(Supplier<T> defaultSupplier);

	@ApiStatus.Internal
	<T extends Persistent> EntityHook<T> doDefinePersistentEntityHook(Supplier<T> defaultSupplier, NamespacedKey key);

	/**
	 * Defines a new non-persistent entity hook, which can be used to store arbitrary data on an entity.
	 * It is recommended to assign the hook to a static final variable at the place of use.
	 * Dynamically defining hooks is discouraged for performance reasons.
	 *
	 * @param <T>             The type of the hook data.
	 * @param defaultSupplier A supplier that provides the default value for the hook.
	 * @return A new entity hook instance.
	 * @author Floweynt
	 * @since 2.0.0
	 */
	static <T> EntityHook<T> entityHook(Supplier<T> defaultSupplier) {
		return getInstance().doDefineEntityHook(defaultSupplier);
	}

	/**
	 * Defines a new persistent entity hook, which can be used to store arbitrary data on an entity.
	 * The hook's data will be saved and loaded automatically.
	 * It is recommended to assign the hook to a static final variable at the place of use.
	 * Dynamically defining hooks is discouraged for performance reasons.
	 *
	 * @param <T>             The type of the persistent hook data.
	 * @param defaultSupplier A supplier that provides the default value for the hook.
	 * @param key             A unique identifier for the hook.
	 * @return A new persistent entity hook instance.
	 * @author Floweynt
	 * @since 2.0.0
	 */
	static <T extends Persistent> EntityHook<T> persistentEntityHook(Supplier<T> defaultSupplier, NamespacedKey key) {
		return getInstance().doDefinePersistentEntityHook(defaultSupplier, key);
	}
}
