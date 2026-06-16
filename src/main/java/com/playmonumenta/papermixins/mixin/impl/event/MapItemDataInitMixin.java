package com.playmonumenta.papermixins.mixin.impl.event;

import com.playmonumenta.papermixins.paperapi.v1.event.MapLoadEvent;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class MapItemDataInitMixin {
	@Redirect(
			method = "getMapData",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/DimensionDataStorage;readSavedData(Ljava/util/function/Function;Lnet/minecraft/util/datafix/DataFixTypes;Ljava/lang/String;)Lnet/minecraft/world/level/saveddata/SavedData;"
			)
	)
	public SavedData emitLoadEvent(
			DimensionDataStorage storage,
			Function<CompoundTag, SavedData> deserializer,
			DataFixTypes dataFixType,
			String id
	) {
		MapLoadEvent mapLoadEvent = new MapLoadEvent(id);
		mapLoadEvent.callEvent();
		if (mapLoadEvent.getData() instanceof CompoundTag dataTag) {
			return MapItemSavedData.load(dataTag);
		}

		return storage.readSavedData(deserializer, dataFixType, id);
	}
}
