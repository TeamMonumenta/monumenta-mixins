package com.playmonumenta.papermixins.mixin.behavior;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
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
@Mixin(SavedDataStorage.class)
public class SavedDataStorageMixin {
	@Inject(
		method = "readSavedData",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
			shift = At.Shift.AFTER
		)
	)
	private <T extends SavedData> void forceExitOnError(SavedDataType<T> type, CallbackInfoReturnable<T> cir) {
		// TODO: lol linux error codes are int8_t
		if (ConfigManager.getConfig().behavior.crashOnScoreboardLoadFail) {
			System.exit(-9001);
		}
	}
}
