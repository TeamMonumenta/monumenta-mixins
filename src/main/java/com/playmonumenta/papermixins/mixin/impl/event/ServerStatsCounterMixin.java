package com.playmonumenta.papermixins.mixin.impl.event;

import com.destroystokyo.paper.event.player.ServerStatsDataLoadEvent;
import com.destroystokyo.paper.event.player.ServerStatsDataSaveEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.io.File;
import java.io.IOException;
import net.minecraft.stats.ServerStatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author NickNackGus
 * <p>
 * Implements stats load/save events so plugins can provide custom data.
 */
@Mixin(ServerStatsCounter.class)
public class ServerStatsCounterMixin {
	@Inject(
		method = "<init>",
		at = @At("HEAD")
	)
	private static void setupStatsLoadEvent(
		CallbackInfo ci,
		@Local(ordinal = 0, argsOnly = true) File fileLocalRef,
		// Introduce new local variables for the event
		@Share("event") LocalRef<ServerStatsDataLoadEvent> eventRef,
		@Share("actualPlayerSavePath") LocalRef<File> actualPlayerSavePath
	) {
		var event = new ServerStatsDataLoadEvent(fileLocalRef);
		eventRef.set(event);
		event.callEvent();
		actualPlayerSavePath.set(event.getPath());
	}

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/io/File;isFile()Z"
		)
	)
	private boolean enableLoadIfEventHasJson(
		boolean original,
		@Share("event") LocalRef<ServerStatsDataLoadEvent> eventRef
	) {
		return original || eventRef.get().getJsonData() != null;
	}

	// Ensure that we log the correct path
	@ModifyArg(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
		),
		index = 1
	)
	private Object modifyLoadLoggedPath(
		Object arg1,
		@Share("actualPlayerSavePath") LocalRef<File> actualPlayerSavePath
	) {
		return actualPlayerSavePath.get().getPath();
	}

	@SuppressWarnings("RedundantThrows")
	@WrapOperation(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/commons/io/FileUtils;readFileToString(Ljava/io/File;)Ljava/lang/String;"
		)
	)
	private String modifyLoadReadSource(
		final File file,
		Operation<String> original,
		@Share("event") LocalRef<ServerStatsDataLoadEvent> eventRef
	) throws IOException {
		String evData = eventRef.get().getJsonData();

		if (evData != null) {
			return evData;
		}
		return original.call(file);
	}

	@SuppressWarnings("RedundantThrows")
	@WrapOperation(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/commons/io/FileUtils;writeStringToFile(Ljava/io/File;Ljava/lang/String;)V"
		)
	)
	private void overrideSave(
		final File file,
		final String data,
		Operation<Void> original
	) throws IOException {
		ServerStatsDataSaveEvent event = new ServerStatsDataSaveEvent(file, data);
		event.callEvent();

		if (event.isCancelled()) {
			return;
		}

		original.call(event.getPath(), event.getJsonData());
	}
}
