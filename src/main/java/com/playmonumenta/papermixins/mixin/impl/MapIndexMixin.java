package com.playmonumenta.papermixins.mixin.impl;

import com.playmonumenta.papermixins.duck.MapIndexAccess;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapIndex.class)
public class MapIndexMixin implements MapIndexAccess {
	@Shadow
	@Final
	private Object2IntMap<String> usedAuxIds;


	@Override
	public int getLastAuxValueForMap() {
		return getUsedAuxIds().getInt("map");
	}
}
