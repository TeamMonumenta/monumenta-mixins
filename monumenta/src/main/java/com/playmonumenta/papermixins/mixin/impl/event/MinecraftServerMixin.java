package com.playmonumenta.papermixins.mixin.impl.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0004-Monumenta-Move-player-saving-to-before-disabling-plu.patch
 * <p>
 * Move player save location.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	@Final
	public static Logger LOGGER;
	@Shadow
	private PlayerList playerList;
	@Shadow
	private volatile boolean isRestarting;

	@Shadow
	private volatile boolean isSaving;

	// Start saving before plugins are disabled
	@Inject(
		method = "stopServer",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/MinecraftServer;server:Lorg/bukkit/craftbukkit/v1_20_R3/CraftServer;",
			ordinal = 0
		)
	)
	void savePlayers(CallbackInfo ci) {
		this.isSaving = true;
		if (this.playerList != null) {
			LOGGER.info("Saving players");
			this.playerList.saveAll();
			this.playerList.removeAll(this.isRestarting);

			try {
				Thread.sleep(100L);
			} catch (InterruptedException ignored) {
			}
		}
	}

	// It's fine to not modify the isSaving = true statement, so we don't bother bonking it

	// Prevent the server from saving players twice
	@ModifyExpressionValue(
		method = "stopServer",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/MinecraftServer;playerList:Lnet/minecraft/server/players/PlayerList;",
			ordinal = 0
		)
	)
	private PlayerList skipSecondSave(PlayerList original) {
		return null;
	}
}
