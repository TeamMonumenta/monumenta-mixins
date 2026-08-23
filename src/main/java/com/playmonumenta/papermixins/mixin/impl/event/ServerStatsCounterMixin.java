package com.playmonumenta.papermixins.mixin.impl.event;

import com.destroystokyo.paper.event.player.ServerStatsDataLoadEvent;
import com.destroystokyo.paper.event.player.ServerStatsDataSaveEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import net.minecraft.stats.ServerStatsCounter;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
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
		@Share("monumenta$actualPlayerSavePath") LocalRef<File> monumenta$actualPlayerSavePath
	) {
		var event = new ServerStatsDataLoadEvent(fileLocalRef);
		eventRef.set(event);
		event.callEvent();
		monumenta$actualPlayerSavePath.set(event.getPath());
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
		@Share("monumenta$actualPlayerSavePath") LocalRef<File> monumenta$actualPlayerSavePath
	) {
		return monumenta$actualPlayerSavePath.get().getPath();
	}

	@Redirect(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/commons/io/FileUtils;readFileToString(Ljava/io/File;)Ljava/lang/String;"
		)
	)
	private String modifyLoadReadSource(
		final File file,
		@Share("event") LocalRef<ServerStatsDataLoadEvent> eventRef
	) throws IOException {
		String evData = eventRef.get().getJsonData();

		if (evData != null) {
			return evData;
		}
		return FileUtils.readFileToString(file, Charset.defaultCharset());
	}

	@Redirect(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/commons/io/FileUtils;writeStringToFile(Ljava/io/File;Ljava/lang/String;)V"
		)
	)
	private void overrideSave(
		final File file,
		final String data
	) throws IOException {
		ServerStatsDataSaveEvent event = new ServerStatsDataSaveEvent(file, data);
		event.callEvent();

		if (event.isCancelled()) {
			return;
		}

		FileUtils.writeStringToFile(event.getPath(), event.getJsonData(), Charset.defaultCharset(), false);
	}
}
