package com.playmonumenta.papermixins.mixin.accessor;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JavaPlugin.class)
public interface JavaPluginAccessor {
	@Invoker("getClassLoader")
	ClassLoader invokeGetClassLoader();

	@Invoker("getFile")
	File invokeGetFile();
}
