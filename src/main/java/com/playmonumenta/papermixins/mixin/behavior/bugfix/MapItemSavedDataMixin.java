package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

/**
 * @author usb
 * Remove CraftBukkit stupidity that crashes the server if the map is invalid
 */
@Mixin(MapItemSavedData.class)
public class MapItemSavedDataMixin {
	// fix CraftBukkit stupidity
	@WrapMethod(method = "lambda$load$1")
	private ResourceKey<Level> fixResourceKey(CompoundTag nbt, Operation<ResourceKey<Level>> original) {
		try {
			ResourceKey<Level> level = original.call(nbt);
			return level == null ? Level.OVERWORLD : level;
		} catch (Exception e) {
			e.printStackTrace();
			return Level.OVERWORLD;
		}
	}
}
