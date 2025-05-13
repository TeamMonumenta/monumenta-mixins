package com.playmonumenta.papermixins.impl.v1.hook;

import com.playmonumenta.mixinapi.v1.hook.Hook;
import com.playmonumenta.mixinapi.v1.hook.Persistent;
import com.playmonumenta.mixinapi.v1.hook.Ticker;
import de.tr7zw.nbtapi.NBTContainer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class HolderBase<A> {
	private final Map<Hook<?, A>, Object> entries = new Object2ObjectLinkedOpenHashMap<>();
	private final A attachment;

	public HolderBase(A attachment) {
		this.attachment = attachment;
	}

	public Collection<?> examine() {
		return Collections.unmodifiableCollection(entries.values());
	}

	@SuppressWarnings("unchecked")
	public <T> @Nullable T get(Hook<T, A> hook) {
		return (T) entries.get(hook);
	}

	public <T> void set(Hook<T, A> hook, T value) {
		entries.put(hook, value);
	}

	@SuppressWarnings("unchecked")
	public <T> @Nullable T remove(Hook<T, A> hook) {
		return (T) entries.remove(hook);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public CompoundTag serialize() {
		final var tag = new CompoundTag();

		entries.forEach((hook, value) -> {
			final var impl = (HookImpl) hook;
			if (!impl.isPersistent()) {
				return;
			}

			final var entryTag = new CompoundTag();
			((Persistent<A>) value).deserialize(new NBTContainer(entryTag), attachment);
			tag.put(impl.id().asString(), entryTag);
		});

		return tag;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void deserialize(CompoundTag tag) {
		for (final var hook : new ArrayList<HookImpl>((Set) entries.keySet())) {
			if (hook.isPersistent()) {
				entries.remove(hook);
			}
		}

		tag.tags.forEach((k, v) -> {
			final var key = NamespacedKey.fromString(k);
			if (key == null) {
				HookAPIImpl.LOGGER.warn("failed to parse key '{}' while loading hooks (not a RL?)", k);
				return;
			}

			final var hook = query(key);

			if (hook == null) {
				HookAPIImpl.LOGGER.warn("unknown hook with id '{}'", k);
				return;
			}

			if (!hook.isPersistent()) {
				HookAPIImpl.LOGGER.warn("hook with id '{}' is not serializable", k);
				return;
			}

			try {
				final var instance = (Persistent<A>) hook.persistenceConstructor().get();
				instance.deserialize(new NBTContainer(v), attachment);
				entries.put(hook, instance);
			} catch (Exception e) {
				HookAPIImpl.LOGGER.warn("while reading hook with id '{}': ", k, e);
			}
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void tick() {
		examine().forEach(object -> {
			if (object instanceof Ticker ticker) {
				ticker.tick(attachment);
			}
		});
	}

	protected abstract HookImpl<?, A> query(NamespacedKey key);

	public static class EntityHolder extends HolderBase<Entity> {
		public EntityHolder(Entity attachment) {
			super(attachment);
		}

		@Override
		protected HookImpl<?, Entity> query(NamespacedKey key) {
			return HookAPIImpl.getInstance().getEntityRegistry().get(key);
		}
	}

	public static class BlockEntityHolder extends HolderBase<TileState> {
		public BlockEntityHolder(TileState attachment) {
			super(attachment);
		}

		@Override
		protected HookImpl<?, TileState> query(NamespacedKey key) {
			return HookAPIImpl.getInstance().getBlockEntityRegistry().get(key);
		}
	}
}
