package com.playmonumenta.papermixins.earlyloader;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * This is the early-loader plugin for mixins. This is useful to inject access widener entries early enough to ensure
 * no classes are loaded yet. In doing so however, we must take care to not load any additional classes.
 */
public class EarlyLoaderPlugin implements IMixinConfigPlugin {
    private final PluginEarlyLoader earlyLoader = new PluginEarlyLoader();

    @Override
    public void onLoad(String s) {
        earlyLoader.doLoad();
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
