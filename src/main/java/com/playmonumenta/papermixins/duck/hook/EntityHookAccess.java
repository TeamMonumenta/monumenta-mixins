package com.playmonumenta.papermixins.duck.hook;

import com.playmonumenta.papermixins.paperapi.v1.HookAPI;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public interface EntityHookAccess {
	Int2ObjectMap<Object> monumenta$getHooks();
	List<Pair<NamespacedKey, HookAPI.Persistent>> monumenta$getPersistentEntries();
	Map<NamespacedKey, CompoundTag> monumenta$getHookPersistentData ();

	static EntityHookAccess instance(Entity bukkitEntity) {
		return (EntityHookAccess) ((CraftEntity) bukkitEntity).getHandle();
	}
}
