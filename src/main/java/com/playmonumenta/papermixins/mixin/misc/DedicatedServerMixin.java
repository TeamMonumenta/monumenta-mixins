package com.playmonumenta.papermixins.mixin.misc;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
	@Inject(
		method = "initServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/spigotmc/SpigotConfig;init(Ljava/io/File;)V"
		)
	)
	private void logOurVersion(CallbackInfoReturnable<Boolean> cir) {
		MonumentaMod.LOGGER.info("Running {}", MonumentaMod.getIdentifier());
	}

	@WrapWithCondition(
		method = "initServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/CraftServer;loadPlugins()V"
		)
	)
	private boolean conditionallyDisablePluginLoading(CraftServer instance) {
		if (MonumentaMod.getConfig().noPlugins) {
			MonumentaMod.LOGGER.info("Disabling plugin loading because of config option 'noPlugins'");
		}

		return !MonumentaMod.getConfig().noPlugins;
	}

	@WrapWithCondition(
		method = "initServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/CraftServer;enablePlugins(Lorg/bukkit/plugin/PluginLoadOrder;)V"
		)
	)
	private boolean conditionallyDisablePluginEnable(CraftServer instance, PluginLoadOrder pluginLoadOrder) {
		return !MonumentaMod.getConfig().noPlugins;
	}
}
