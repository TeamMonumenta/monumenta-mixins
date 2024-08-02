import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke

plugins {
    id("com.playmonumenta.paperweight-aw.userdev")
    id("com.playmonumenta.papermixins.java-conventions")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

dependencies {
    // Paper & ignite - toolchain items
    compileOnly(libs.ignite.api)
    compileOnly(libs.bundles.mixin)
    compileOnly(paperweight.paperDevBundle(libs.versions.paper.api.get()))
    implementation(libs.paper.server)
    implementation(libs.ignite.launcher)
    implementation(libs.bundles.papermisc) // Required for server to start when running "Minecraft Server"
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
        archiveClassifier.set("")
    }

    reobfJar {
        remapperArgs.add("--mixin")
        finalizedBy("remapAccessWidener")
    }

    build {
        dependsOn(reobfJar)
    }
}
