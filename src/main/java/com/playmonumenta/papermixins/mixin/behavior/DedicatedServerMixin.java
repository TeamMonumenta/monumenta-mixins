package com.playmonumenta.papermixins.mixin.behavior;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
	@WrapWithCondition(
		method = "initServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/CraftServer;loadPlugins()V"
		)
	)
	private boolean conditionallyDisablePluginLoading(CraftServer instance) {
		if (ConfigManager.getConfig().noPlugins) {
			MonumentaMod.LOGGER.info("Disabling plugin loading because of config option 'noPlugins'");
		}

		return !ConfigManager.getConfig().noPlugins;
	}

	@WrapWithCondition(
		method = "initServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/CraftServer;enablePlugins(Lorg/bukkit/plugin/PluginLoadOrder;)V"
		)
	)
	private boolean conditionallyDisablePluginEnable(CraftServer instance, PluginLoadOrder pluginLoadOrder) {
		return !ConfigManager.getConfig().noPlugins;
	}
}
