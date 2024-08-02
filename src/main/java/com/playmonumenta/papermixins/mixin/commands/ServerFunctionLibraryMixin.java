package com.playmonumenta.papermixins.mixin.commands;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0018-Monumenta-Ensure-minecraft-reload-uses-latest-Brigad.patch
 * <p>
 * Remove a bunch of CommandAPI errors.
 */
@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {
    @Unique
    private static boolean monumenta$isInitialFunctionLoad = true;

    @Redirect(
        method = "lambda$reload$1",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/FileToIdConverter;listMatchingResources" +
                "(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
        )
    )
    private static Map<ResourceLocation, Resource> disableFunctionOnFirstLoad(FileToIdConverter instance,
                                                                              ResourceManager resourceManager) {
        if (monumenta$isInitialFunctionLoad) {
            return Map.of();
        } else {
            return instance.listMatchingResources(resourceManager);
        }
    }

    @Inject(
        method = "lambda$reload$7",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
        )
    )
    private void onReload(Pair<?, ?> intermediate, CallbackInfo ci) {
        monumenta$isInitialFunctionLoad = false;
    }
}