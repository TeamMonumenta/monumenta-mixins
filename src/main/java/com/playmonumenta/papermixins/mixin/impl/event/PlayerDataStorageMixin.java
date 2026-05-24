package com.playmonumenta.papermixins.mixin.impl.event;

import com.llamalad7.mixinextras.sugar.Share;
import com.playmonumenta.papermixins.paperapi.v1.event.PlayerDataLoadEvent;
import com.playmonumenta.papermixins.paperapi.v1.event.PlayerDataSaveEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.playmonumenta.papermixins.util.Util;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

	@Inject(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Ljava/nio/file/Files;createTempFile(Ljava/nio/file/Path;Ljava/lang/String;Ljava/lang/String;" +
				"[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;"
		),
		cancellable = true
	)
	private void emitSaveEvent(Player player, CallbackInfo ci,
							   @Local(name = "playerDirPath") Path playerDirPath,
							   @Local(name = "output") TagValueOutput output,
							   @Share("overrideSavePath") LocalRef<Path> overrideSavePath) {
		var event = new PlayerDataSaveEvent(
			(CraftPlayer) player.getBukkitEntity(),
			playerDirPath.resolve(player.getStringUUID() + ".dat"),
			output.buildResult()
		);

		overrideSavePath.set(null);

		// if it's cancelled, we don't need to write the file
		if (!event.callEvent()) {
			ci.cancel();
		}

		overrideSavePath.set(event.getPath());
	}

	@Redirect(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;",
			ordinal = 0
		)
	)
	private Path modifyPathArg(Path instance, String other, @Share("overrideSavePath") LocalRef<Path> path) {
		return path.get();
	}

	@Inject(
		method = "load(Lnet/minecraft/server/players/NameAndId;Ljava/lang/String;)Ljava/util/Optional;",
		at = @At(
			value = "INVOKE",
			target = "Ljava/io/File;exists()Z"
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"
			),
			to = @At(
				value = "INVOKE",
				target = "Ljava/io/File;isFile()Z"
			)
		),
		cancellable = true
	)
	private void emitLoadEvent(NameAndId nameAndId, String suffix, CallbackInfoReturnable<Optional<CompoundTag>> cir,
							   @Local(argsOnly = true) NameAndId player,
							   @Local(name = "realFile") LocalRef<File> realFile) {
		PlayerDataLoadEvent event = new PlayerDataLoadEvent(new CraftOfflinePlayer(
			MinecraftServer.getServer().server,
			player
		), realFile.get().toPath());

		event.callEvent();

		realFile.set(event.getPath().toFile());

		if (event.getData() != null) {
			cir.setReturnValue(Optional.of(Util.c(event.getData())));
		}
	}
}
