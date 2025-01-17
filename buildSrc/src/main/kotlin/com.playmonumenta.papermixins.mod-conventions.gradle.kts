import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.invoke

plugins {
    id("com.playmonumenta.paperweight-aw.userdev")
    id("com.playmonumenta.papermixins.java-conventions")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

val include: Configuration by configurations.creating

configurations.getByName("implementation").extendsFrom(include)
configurations.getByName("implementation").extendsFrom(configurations.getByName("mojangMappedServerRuntime"))

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())

    implementation(libs.fabric.loader)

    remapper(libs.tiny.remapper) { // Tiny remapper
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
