package com.playmonumenta.papermixins.mixin.itemapi;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.impl.v1.resource.PluginDataListener;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @ModifyExpressionValue(
        method = "listeners",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;" +
                "Ljava/lang/Object;)Ljava/util/List;"
        )
    )
    private List<PreparableReloadListener> addCustomItemAPILoader(List<PreparableReloadListener> original) {
        return Stream.of(original, List.of(PluginDataListener.INSTANCE))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
}
