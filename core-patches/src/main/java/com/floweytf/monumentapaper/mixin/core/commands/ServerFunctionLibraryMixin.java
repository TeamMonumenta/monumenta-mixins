package com.floweytf.monumentapaper.mixin.core.commands;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author Flowey
 * @mm-patch 0018-Monumenta-Ensure-minecraft-reload-uses-latest-Brigad.patch
 * <p>
 * Remove a bunch of CommandAPI errors.
 */
@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
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
    private static Map<ResourceLocation, Resource> monumenta$disableFunctionOnFirstLoad(FileToIdConverter instance,
                                                                                        ResourceManager resourceManager) {
        if (monumenta$isInitialFunctionLoad) {
            return new FileToIdConverter("functions", ".nope.nope.nope.nope.Monumenta.nope")
                .listMatchingResources(resourceManager);
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
    private void monumenta$onReload(Pair<?, ?> intermediate, CallbackInfo ci) {
        monumenta$isInitialFunctionLoad = false;
    }
}