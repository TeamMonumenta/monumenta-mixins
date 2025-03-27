@file:Suppress("UnstableApiUsage")

package com.playmonumenta.uniminedpaper

import com.google.gson.JsonParser
import io.sigpipe.jbsdiff.Patch
import org.gradle.api.Project
import xyz.wagyourtail.unimined.api.minecraft.MinecraftConfig
import xyz.wagyourtail.unimined.api.minecraft.MinecraftJar
import xyz.wagyourtail.unimined.api.minecraft.patch.MergedPatcher
import xyz.wagyourtail.unimined.api.unimined
import xyz.wagyourtail.unimined.internal.minecraft.MinecraftProvider
import xyz.wagyourtail.unimined.internal.minecraft.patch.AbstractMinecraftTransformer
import xyz.wagyourtail.unimined.internal.minecraft.patch.merged.MergedMinecraftTransformer
import xyz.wagyourtail.unimined.mapping.Namespace
import xyz.wagyourtail.unimined.mapping.jvms.four.two.one.InternalName
import xyz.wagyourtail.unimined.mapping.tree.node._class.ClassNode
import xyz.wagyourtail.unimined.mapping.visitor.fixes.renest
import xyz.wagyourtail.unimined.util.cachingDownload
import xyz.wagyourtail.unimined.util.openZipFileSystem
import xyz.wagyourtail.unimined.util.readZipContents
import xyz.wagyourtail.unimined.util.withSourceSet
import java.net.URI
import kotlin.io.path.*

private fun paperDownload(ver: String, build: Int) =
	"https://api.papermc.io/v2/projects/paper/versions/${ver}/builds/${build}/downloads/paper-${ver}-${build}.jar"

class PaperTransformer(project: Project, mc: MinecraftProvider, val build: Int) :
	AbstractMinecraftTransformer(
		project,
		mc,
		"paper"
	) {

	private val mojmappedProd = mc.minecraftData.mcVersionCompare(mc.version, "1.20.6") >= 0

	// important files
	private val cache = project.unimined.getLocalCache(mc.sourceSet).resolve("paper").resolve(mc.version)

	private val paperclipJar by lazy {
		project.cachingDownload(
			URI(paperDownload(mc.version, build)),
			cachePath = cache.resolve("paperclip.jar")
		)
	}

	private val buildInfo by lazy {
		val buildInfoFile = project.cachingDownload(
			URI("https://hub.spigotmc.org/versions/${mc.version}.json"),
			cachePath = cache.resolve("build-info.json")
		)

		val buildInfoJson = JsonParser.parseString(buildInfoFile.readText())
		buildInfoJson.asJsonObject
	}

	private val buildDataZip by lazy {
		val buildDataZip = cache.resolve("build-data.zip")
		if (!buildDataZip.exists() || project.unimined.forceReload) {
			val version = buildInfo["refs"].asJsonObject["BuildData"].asString
			project.cachingDownload(
				URI.create("https://hub.spigotmc.org/stash/rest/api/latest/projects/SPIGOT/repos/builddata/archive?at=$version&format=zip"),
				cachePath = buildDataZip
			)
		}
		buildDataZip
	}

	private val paperPatchedJar by lazy {
		val patchedPaperJar = cache.resolve("paper.jar")
		if (!patchedPaperJar.exists() || project.unimined.forceReload) {
			project.logger.lifecycle("[Unimined/Paper] Generating patched minecraft jar with Paperclip.")
			paperclipJar.openZipFileSystem().use { fs ->
				patchedPaperJar.outputStream().use { os ->
					Patch.patch(
						provider.minecraftData.minecraftServerFile.readBytes(),
						fs.getPath("/META-INF/versions/${mc.version}/server-${mc.version}.jar.patch").readBytes(),
						os
					)
				}
			}
		}
		patchedPaperJar
	}

	override fun beforeMappingsResolve() {
		provider.mappings {
			val paperClasses = mutableListOf<String>()

			for (string in paperPatchedJar.readZipContents()) {
				if (string.endsWith(".class")) {
					paperClasses.add(string.substring(0, string.length - ".class".length))
				}
			}

			val srcName = if (mojmappedProd) "mojmap" else "target"

			postProcessDependency("paperProd", {
				if (mojmappedProd) {
					mojmap()
				} else {
					mapping(buildDataZip.toFile(), "spigotProd") {
						mapNamespace("source", "official")
						provides("target" to true)
					}
				}

				afterLoad.add {
					val prodMappings = Namespace(srcName)
					val official = Namespace("official")

					for (cls in paperClasses) {
						// already in
						if (getClass(prodMappings, InternalName.unchecked(cls)) != null) continue

						// find parent in mappings
						var c: ClassNode? = null
						var parentName = cls
						while (c == null && parentName.contains("$")) {
							parentName = parentName.substring(0, parentName.lastIndexOf("$"))
							c = getClass(prodMappings, InternalName.unchecked(parentName))
						}

						// found parent
						if (c != null) {
							val parentOfficialName = c.getName(Namespace("official")).toString()
							// add mapping
							visitClass(
								official to InternalName.unchecked(parentOfficialName + cls.removePrefix(parentName)),
								prodMappings to InternalName.unchecked(cls)
							)?.visitEnd()
						}
					}
				}
			}) {
				provides("paperProd" to true)
				mapNamespace(srcName, "paperProd")
			}

			afterLoad.add {
				// force renest every namespace
				renest(Namespace("official"), namespaces.toSet() - Namespace("official"))
			}
		}

		super.beforeMappingsResolve()
	}

	override fun apply() {
		val paperLibs = project.configurations.maybeCreate("paperLibraries".withSourceSet(provider.sourceSet)).apply {
			provider.sourceSet.compileClasspath += this
			provider.sourceSet.runtimeClasspath += this
		}

		paperclipJar.openZipFileSystem().use {
			it.getPath("/META-INF/libraries.list").readLines().forEach { line ->
				val artifact = line.split("\t")[1]
				paperLibs.dependencies.add(project.dependencies.create(artifact))
			}
		}
	}

	override fun transform(minecraft: MinecraftJar): MinecraftJar {
		if (minecraft.envType != xyz.wagyourtail.unimined.mapping.EnvType.SERVER) {
			throw IllegalArgumentException("Craftbukkit can only be applied to server jars")
		}

		val patchedJar = MinecraftJar(
			minecraft,
			name = providerName,
			// ignore previous patches because they get skipped... thanks paper for distributing a binary delta
			patches = listOf("paper"),
			mappingNamespace = Namespace("paperProd")
		)

		// copy the patched paper jar
		paperPatchedJar.copyTo(patchedJar.path, true)

		return super.transform(patchedJar)
	}
}

fun MinecraftConfig.paper(build: Int, action: PaperTransformer.() -> Unit = {}) {
	(this as MinecraftProvider).apply {
		customPatcher(PaperTransformer(project, this, build), action)
	}
}

fun MergedPatcher.paper(build: Int, action: PaperTransformer.() -> Unit = {}) {
	(this as MergedMinecraftTransformer).apply {
		customPatcher(PaperTransformer(provider.project, provider, build), action)
	}
}

