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

val modImplementation by configurations.creating
val include by configurations.creating

configurations.getByName("runtimeClasspath").extendsFrom(modImplementation)
configurations.getByName("compileClasspath").extendsFrom(modImplementation)
modImplementation.extendsFrom(include)

dependencies {
    // Paper & ignite - toolchain items
    // TODO: what the fuck?
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