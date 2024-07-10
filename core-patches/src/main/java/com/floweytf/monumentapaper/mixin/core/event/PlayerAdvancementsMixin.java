package com.floweytf.monumentapaper.mixin.core.event;

import com.floweytf.monumentapaper.api.event.PlayerAdvancementDataLoadEvent;
import com.floweytf.monumentapaper.api.event.PlayerAdvancementDataSaveEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.FileUtil;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

/**
 * @author Flowey
 * @mm-patch 0002-Monumenta-Add-events-for-loading-and-saving-advancem.patch
 * <p>
 * Implements advancements load/save events so plugins can provide custom data.
 */
@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {
    @Shadow
    @Final
    private static Gson GSON;
    @Unique
    private final ThreadLocal<File> monumenta$actualPlayerSavePath = new ThreadLocal<>();
    @Shadow
    private ServerPlayer player;
    @Shadow
    @Final
    private Path playerSavePath;

    // Remove pretty printing for some reason
    // Basically, we redirect a call to setPrettyPrinting to a noop
    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/gson/GsonBuilder;setPrettyPrinting()Lcom/google/gson/GsonBuilder;"
        )
    )
    private static GsonBuilder monumenta$removePrettyPrint(GsonBuilder builder) {
        return builder;
    }

    // Emit and set up the advancement load event
    @Inject(
        method = "load",
        at = @At("HEAD")
    )
    private void monumenta$setupAdvancementLoadEvent(
        CallbackInfo ci,
        // Introduce a new local variable for the event
        @Share("event") LocalRef<PlayerAdvancementDataLoadEvent> eventRef
    ) {
        var event = new PlayerAdvancementDataLoadEvent(this.player.getBukkitEntity(), this.playerSavePath.toFile());
        eventRef.set(event);
        event.callEvent();
        monumenta$actualPlayerSavePath.set(event.getPath());
    }

    @ModifyArg(
        method = "lambda$applyFrom$0",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        ),
        index = 2
    )
    private Object monumenta$logActualSavePath(Object arg1) {
        return monumenta$actualPlayerSavePath.get();
    }

    // Allow loading even if file doesn't exist as long as event supplies json
    @ModifyExpressionValue(
        method = "load",
        at = @At(
            value = "INVOKE",
            target = "Ljava/nio/file/Files;isRegularFile(Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z"
        )
    )
    private boolean monumenta$enableLoadIfEventHasJson(
        boolean original,
        @Share("event") LocalRef<PlayerAdvancementDataLoadEvent> eventRef
    ) {
        return original || eventRef.get().getJsonData() != null;
    }

    // Ensure that we log the correct path
    @ModifyArg(
        method = "load",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        ),
        index = 1
    )
    private Object monumenta$modifyLoadLoggedPath(
        Object arg1,
        @Share("event") LocalRef<PlayerAdvancementDataLoadEvent> eventRef
    ) {
        return eventRef.get().getPath();
    }

    // We should use the data supplied by the event if possible
    @ModifyArg(
        method = "load",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/gson/stream/JsonReader;<init>(Ljava/io/Reader;)V"
        ),
        index = 0
    )
    private Reader monumenta$modifyLoadReadSource(
        Reader reader,
        @Share("event") LocalRef<PlayerAdvancementDataLoadEvent> eventRef
    ) {
        var evData = eventRef.get().getJsonData();
        if (evData != null) {
            return new StringReader(evData);
        }

        return reader;
    }

    // Fix logging, this isn't really idea
    // I'd like to avoid doing this in the future
    @ModifyArg(method = "lambda$applyFrom$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn" +
        "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 1, index = 2)
    private Object monumenta$modifyLoadLoggedPath(Object arg) {
        return monumenta$actualPlayerSavePath;
    }

    // Save event implementation
    // I'm not going to explain how this works, maybe I'll document this later...

    // Emit event
    // NOTE: this is some really cursed mixin technique, please avoid doing this
    // I'm way too tired to come up with a more sane injector
    @ModifyExpressionValue(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Util;getOrThrow(Lcom/mojang/serialization/DataResult;" +
                "Ljava/util/function/Function;)Ljava/lang/Object;"
        )
    )
    private Object monumenta$setupSaveEvent(
        Object original,
        // create event local variable
        @Share("event") LocalRef<PlayerAdvancementDataSaveEvent> eventRef
    ) {
        var jsonelement = (JsonElement) original;
        var event = new PlayerAdvancementDataSaveEvent(this.player.getBukkitEntity(), this.playerSavePath.toFile(),
            GSON.toJson(jsonelement));
        eventRef.set(event);

        return original;
    }

    @Inject(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/FileUtil;createDirectoriesSafe(Ljava/nio/file/Path;)V"
        ),
        cancellable = true
    )
    private void monumenta$cancelSaveEvent(
        CallbackInfo ci,
        @Share("event") LocalRef<PlayerAdvancementDataSaveEvent> eventRef
    ) {
        if (!eventRef.get().callEvent())
            ci.cancel();
    }

    @Redirect(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/FileUtil;createDirectoriesSafe(Ljava/nio/file/Path;)V"
        )
    )
    private void monumenta$createSaveDir(
        Path path,
        @Share("event") LocalRef<PlayerAdvancementDataSaveEvent> eventRef
    ) throws IOException {
        var parentPath = eventRef.get().getPath().getParentFile();
        if (parentPath != null) {
            FileUtil.createDirectoriesSafe(parentPath.toPath());
        }
    }

    @ModifyArg(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Ljava/nio/file/Files;newBufferedWriter(Ljava/nio/file/Path;Ljava/nio/charset/Charset;" +
                "[Ljava/nio/file/OpenOption;)Ljava/io/BufferedWriter;"
        ),
        index = 0
    )
    private Path monumenta$modifySaveFileDestination(
        Path path,
        @Share("event") LocalRef<PlayerAdvancementDataSaveEvent> eventRef
    ) {
        return eventRef.get().getPath().toPath();
    }

    @ModifyArg(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        ),
        index = 1
    )
    private Object monumenta$modifySaveLoggedPath(
        Object ignored,
        @Share("event") LocalRef<PlayerAdvancementDataSaveEvent> eventRef
    ) {
        return eventRef.get().getPath().toPath();
    }
}