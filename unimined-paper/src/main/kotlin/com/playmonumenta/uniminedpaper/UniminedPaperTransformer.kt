package com.playmonumenta.uniminedpaper

import org.gradle.api.Project
import xyz.wagyourtail.unimined.api.minecraft.EnvType
import xyz.wagyourtail.unimined.api.minecraft.MinecraftJar
import xyz.wagyourtail.unimined.api.unimined
import xyz.wagyourtail.unimined.internal.minecraft.MinecraftProvider
import xyz.wagyourtail.unimined.internal.minecraft.patch.AbstractMinecraftTransformer

class UniminedPaperTransformer(project: Project, provider: MinecraftProvider) : AbstractMinecraftTransformer(
    project,
    provider,
    "PaperFabric"
) {
    val cache by lazy {
        project.unimined.getLocalCache(provider.sourceSet).resolve("paperfabric")
    }

    override fun beforeMappingsResolve() {
        super.beforeMappingsResolve()
    }

    override fun transform(minecraft: MinecraftJar): MinecraftJar {
        if (minecraft.envType != EnvType.SERVER) {
            throw IllegalArgumentException("Craftbukkit can only be applied to server jars")
        }

        val outputFile = executor.runBuildTools()

        // copy output file to
        val patchedJar = MinecraftJar(
            minecraft,
            name = providerName,
            patches = minecraft.patches + listOf(executor.version + "-${executor.buildInfo.name}"),
            mappingNamespace = provider.mappings.getNamespace("spigot_prod"),
        )

        outputFile.copyTo(patchedJar.path, overwrite = true)

        return super.transform(patchedJar)
    }
}