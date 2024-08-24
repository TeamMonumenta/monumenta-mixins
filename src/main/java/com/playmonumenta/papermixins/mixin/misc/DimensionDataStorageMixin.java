package com.playmonumenta.papermixins.mixin.misc;

import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0010-Monumenta-Refuse-to-start-shard-if-scoreboard-fails-.patch
 * <p>
 * Exit on scoreboard load failure
 */
@Mixin(DimensionDataStorage.class)
public class DimensionDataStorageMixin {
	@Inject(
		method = "readSavedData",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
			shift = At.Shift.AFTER
		)
	)
	private <T> void forceExitOnError(Function<CompoundTag, T> readFunction, DataFixTypes dataFixTypes,
												String id, CallbackInfoReturnable<T> cir) {
		// TODO: lol linux error codes are int8_t
		System.exit(-9001);
	}
}
