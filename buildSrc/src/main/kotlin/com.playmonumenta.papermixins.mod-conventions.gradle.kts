import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import java.io.PrintStream

plugins {
    id("com.playmonumenta.paperweight-aw.userdev")
    id("com.playmonumenta.papermixins.java-conventions")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

// Okay, time to explain this mess
// In the land of fabric, there lives the KnotClassDelegate.
// KnotClassDelegate is responsible for figuring out which classes to load from, and where
// In runtime, this works perfectly fine; classes are loaded from where they are specified with the correct
// URLClassLoader, and we don't have to worry about classpath contamination since the entire launcher is bundled
// in a nice shadowJar.
//
// However, the development enviroment is an entirely different world, defined by ugly hacks and weird classloading.
// Here, all classes are in specified via JVM args to be in the classpath of the classloader that launches Main
// This is a problem, since this behavior is *very* different than production.
// Fabric has a solution: we can specify which classes *sources* (jars, directories) shall be loaded from the parent
// classloader. However, this is also very fragile: one wrong jar whitelisted and everything breaks.
//
// The only places we want to load classes with the parent classloader are:
// - Paper, since we want the DEOBF, MOJMAPPED paper. If we don't, fabric will use the paper found by my game
//   locator, generated by run/paperclip.jar, in run/versions/<version>/XXX.jar. We MUST exclude transitive
//   deps, since fabric depends on ASM, and fabric/knot can't load the classes fabric itself depends on. Note that
//   this is also an issue when developing the loader for runtime, and is properly handled (by filter on filename)
// - Mixin Extras
// - The mod's classes itself (build/classes/java/main, basically wherever gradle emits compiled classes)
// - The mod's dependencies
//
// For ease of dev, we provide `include` and `modImplementation`. Include is just `modImplementation` but also
// adds it to the shadow dependency.
//
// Specifically (src -> dest means all dependencies from src are added to dest)
// modImplementation -> implementation
// include -> modImplementation, shadow
//
// NOTE: you need run/paperclip.jar for development enviroment to work, not sure how loom solves this issue.
// TODO: research loom & fabric a bit more
val modImplementation by configurations.creating
val include by configurations.creating

configurations.getByName("implementation").extendsFrom(modImplementation)
modImplementation.extendsFrom(include)

dependencies {
    modImplementation(libs.paper.server) {
        isTransitive = false
    }
    implementation(libs.paper.server)
    implementation(libs.fabricloader)
    modImplementation(libs.mixin.extras)
    runtimeOnly(libs.dli)
    compileOnly(paperweight.paperDevBundle(libs.versions.paper.api.get()))
    implementation(libs.bundles.paperrt) // Required for server to start when running "Minecraft Server"
    remapper(libs.tinyremapper) { // Tiny remapper
        artifact {
            classifier = "fat"
        }
    }
}

tasks {
    jar {
        archiveClassifier.set("dev")
    }

    shadowJar {
        archiveClassifier.set("dev")
        configurations = listOf(include)
    }

    reobfJar {
        remapperArgs.add("--mixin")
        finalizedBy("remapAccessWidener")
    }

    build {
        dependsOn(reobfJar)
    }
}

// Set up DLI
// DLI is just a way to set VM properties before launching a target class, this is nice because
// we can regenerate dli.config without touching the intellij runs.
// Don't worry about this.
afterEvaluate {
    val dliFile = layout.projectDirectory.asFile.resolve(".gradle").resolve("dli.config")

    val buildDir = layout.buildDirectory.get().asFile
    val javaClasses = buildDir.resolve("classes").resolve("java").resolve("main").absolutePath
    val resources = buildDir.resolve("resources").resolve("main").absolutePath

    if(!dliFile.exists()) {
        dliFile.createNewFile()
    }

    val jarDeps = modImplementation.files.joinToString(":")

    PrintStream(dliFile).use {
        it.println("commonProperties")
        it.println("\tfabric.development=true")
        it.println("\tfabric.classPathGroups=${javaClasses}:${resources}:${jarDeps}")
    }
}