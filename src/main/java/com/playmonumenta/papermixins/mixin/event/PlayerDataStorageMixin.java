package com.playmonumenta.papermixins.mixin.event;

import com.destroystokyo.paper.event.player.PlayerDataLoadEvent;
import com.destroystokyo.paper.event.player.PlayerDataSaveEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.playmonumenta.papermixins.MonumentaMod;
import java.io.File;
import java.nio.file.Files;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0003-Monumenta-Add-events-for-loading-and-saving-player-d.patch
 * <p>
 * Implements player load/save events so plugins can provide custom data
 */
@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin {
    @Shadow
    @Final
    private File playerDir;

    // really cursed save inject
    // TODO: break apart this inject or just use overwrite
    @Inject(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Ljava/nio/file/Files;createTempFile(Ljava/nio/file/Path;Ljava/lang/String;Ljava/lang/String;" +
                "[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;"
        ),
        cancellable = true
    )
    private void emitSaveEvent(Player player, CallbackInfo ci, @Local(ordinal = 0) CompoundTag tag) {
        // always cancel the event since we overwrite logic
        ci.cancel();

        var playerData = new File(this.playerDir, player.getStringUUID() + ".dat");
        var playerDataOld = new File(this.playerDir, player.getStringUUID() + ".dat_old");

        var event = new PlayerDataSaveEvent((CraftPlayer) (player.getBukkitEntity()), playerData, tag);

        if (!event.callEvent()) {
            return;
        }

        try {
            var file = Files.createTempFile(this.playerDir.toPath(), player.getStringUUID() + "-", ".dat");
            NbtIo.writeCompressed((CompoundTag) event.getData(), file);
            Util.safeReplaceFile(event.getPath().toPath(), file, playerDataOld.toPath());
            ci.cancel();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @ModifyVariable(
        method = "load",
        at = @At(value = "LOAD", ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V"
            ),
            to = @At(
                value = "INVOKE:LAST",
                target = "Ljava/io/File;exists()Z"
            )
        )
    )
    private File emitLoadEvent(File file, Player player, @Local LocalRef<CompoundTag> tag) {
        PlayerDataLoadEvent event = new PlayerDataLoadEvent((CraftPlayer) (player.getBukkitEntity()), file);
        event.callEvent();

        if (event.getData() != null) {
            tag.set((CompoundTag) event.getData());
            return MonumentaMod.FAKE_FILE;
        }

        return event.getPath();
    }
}
