import gradle.kotlin.dsl.accessors._38fa8ead452481b64a55a9c452402ffd.shadowJar
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.invoke

plugins {
    id("com.playmonumenta.paperweight-aw.userdev")
    id("com.playmonumenta.papermixins.java-conventions")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

configurations.getByName("runtimeClasspath").extendsFrom(configurations.getByName("mojangMappedServerRuntime"))
configurations.getByName("runtimeClasspath").extendsFrom(configurations.getByName("mojangMappedServer"))

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
    }

    reobfJar {
        remapperArgs.add("--mixin")
        finalizedBy("remapAccessWidener")
    }

    build {
        dependsOn(reobfJar)
    }
}
