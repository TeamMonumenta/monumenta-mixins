package com.playmonumenta.papermixins;

import com.playmonumenta.papermixins.earlyloader.PluginEarlyLoader;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * This is the early-loader plugin for mixins. This is useful for performing a wide variety of loader-related tasks,
 * such as injecting access widener entries early enough to ensure classes are loaded yet. In doing so however, we
 * must take care to not load any additional classes.
 */
public class MonumentaMixinConfigPlugin implements IMixinConfigPlugin {
    private static final String BRIG_PATH = "libraries/com/mojang/brigadier/1.2.9/brigadier-1.2.9.jar";
    private final PluginEarlyLoader earlyLoader = new PluginEarlyLoader();

    @Override
    public void onLoad(String s) {
        earlyLoader.doLoad();

        // polyfill api
        final var launcher = FabricLauncherBase.getLauncher();
        if(!launcher.isDevelopment()) {
            launcher.getClassPath()
                .stream()
                .filter(x -> x.toAbsolutePath().toString().endsWith(BRIG_PATH))
                .forEach(x -> launcher.setAllowedPrefixes(x, "__yeah_fuck_you_dont_load_old_brig_please!!"));
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
