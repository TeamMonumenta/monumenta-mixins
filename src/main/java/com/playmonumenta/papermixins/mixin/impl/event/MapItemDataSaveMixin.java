package com.playmonumenta.papermixins.mixin.impl.event;

import com.playmonumenta.papermixins.paperapi.v1.event.MapSaveEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItemSavedData.class)
public class MapItemDataSaveMixin {
	@Shadow
	public String id;

	@Inject(
			method = "save",
			at = @At(value = "TAIL")
	)
	public void emitSaveEvent(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
		MapSaveEvent mapSaveEvent = new MapSaveEvent(id, nbt);
		mapSaveEvent.callEvent();
	}
}
