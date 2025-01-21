package com.playmonumenta.papermixins.mixin.impl.hook;

import com.playmonumenta.papermixins.duck.hook.EntityHookAccess;
import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import de.tr7zw.nbtapi.NBTContainer;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.bukkit.NamespacedKey;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements EntityHookAccess {
	@Shadow @Final private static Logger LOGGER;
	@Unique
	private final Int2ObjectMap<Object> monumenta$hooks = new Int2ObjectOpenHashMap<>();

	@Unique
	private final List<Pair<NamespacedKey, HookAPI.Persistent>> monumenta$persistentEntries = new ArrayList<>();

	@Unique
	private final Map<NamespacedKey, CompoundTag> monumenta$hookPersistentData = new HashMap<>();

	@Override
	public Int2ObjectMap<Object> monumenta$getHooks() {
		return monumenta$hooks;
	}

	@Override
	public List<Pair<NamespacedKey, HookAPI.Persistent>> monumenta$getPersistentEntries() {
		return monumenta$persistentEntries;
	}

	@Override
	public Map<NamespacedKey, CompoundTag> monumenta$getHookPersistentData() {
		return monumenta$hookPersistentData;
	}

	@Inject(
		method = "load",
		at = @At("RETURN")
	)
	private void loadCustom(CompoundTag nbt, CallbackInfo ci) {
		nbt.getCompound("monumenta:hooks").tags.forEach((key, value) -> {
			try {
				monumenta$hookPersistentData.put(NamespacedKey.fromString(key), (CompoundTag) value);
			} catch (Exception e) {
				LOGGER.warn("Failed to load persistent hook {}", key, e);
			}
		});
	}

	@Inject(
		method = "saveWithoutId(Lnet/minecraft/nbt/CompoundTag;Z)Lnet/minecraft/nbt/CompoundTag;",
		at = @At("RETURN")
	)
	private void saveCustom(CompoundTag rootTag, boolean includeAll, CallbackInfoReturnable<CompoundTag> cir) {
		final var persistentHookRoot = new CompoundTag();

		monumenta$hookPersistentData.forEach((key, data) -> {
			persistentHookRoot.put(key.asString(), data);
		});

		monumenta$persistentEntries.forEach(entry -> {
			final var serTag = new CompoundTag();
			entry.value().save(new NBTContainer(serTag));
			persistentHookRoot.put(entry.key().asString(), serTag);
		});

		rootTag.put("monumenta:hooks", persistentHookRoot);
	}
}
