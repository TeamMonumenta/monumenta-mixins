import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke

plugins {
    id("com.playmonumenta.paperweight-aw.userdev")
    id("com.playmonumenta.papermixins.java-conventions")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

val shadowImplementation by configurations.creating

dependencies {
    // Paper & ignite - toolchain items
    implementation(libs.bundles.toolchain)
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
        configurations = listOf(shadowImplementation)
    }

    reobfJar {
        remapperArgs.add("--mixin")
        finalizedBy("remapAccessWidener")
    }

    build {
        dependsOn(reobfJar)
    }
}
