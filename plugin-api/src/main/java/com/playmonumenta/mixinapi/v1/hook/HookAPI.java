package com.playmonumenta.mixinapi.v1.hook;

import com.playmonumenta.mixinapi.v1.MonumentaPaperAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The Hook API allows attaching arbitrary objects to entities or other targets.
 * This API provides mechanisms to define hooks that can be used to store and retrieve custom data on entities.
 * <p>
 * Hooks should typically not be dynamically defined, but instead should be assigned to static final variables
 * where they are used. This ensures better performance and avoids unnecessary redefinition of hooks.
 * <p>
 * Hooks must be defined at the proper phase in initialization, which is during plugin boostrap.
 *
 * @author Floweynt
 * @since 3.0.0
 */
@ApiStatus.NonExtendable
public interface HookAPI {
	@NotNull
	@ApiStatus.Internal
	static HookAPI getInstance() {
		return MonumentaPaperAPI.getInstance().getHookAPI();
	}

	@ApiStatus.Internal
	HookRegistry<Entity> getEntityRegistry();

	@ApiStatus.Internal
	HookRegistry<TileState> getBlockEntityRegistry();

	@ApiStatus.Internal
	<T, A> Hook<T, A> doDefine(NamespacedKey key, Class<T> clazz);

	static HookRegistry<Entity> entityRegistry() {
		return getInstance().getEntityRegistry();
	}

	static HookRegistry<TileState> blockEntityRegistry() {
		return getInstance().getBlockEntityRegistry();
	}

	static <T> Hook<T, Entity> defineEntity(NamespacedKey key, Class<T> clazz) {
		return getInstance().doDefine(key, clazz);
	}

	static <T> Hook<T, TileState> defineBlockEntity(NamespacedKey key, Class<T> clazz) {
		return getInstance().doDefine(key, clazz);
	}
}
