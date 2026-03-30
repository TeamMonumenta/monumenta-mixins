import io.papermc.paperweight.tasks.TinyRemapper
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.playmonumenta.paperweight-aw.userdev")
    id("com.gradleup.shadow")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

val include: Configuration by configurations.creating
val shade: Configuration by configurations.creating
shade.extendsFrom(include)
configurations.getByName("implementation").extendsFrom(include)

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://repo.spongepowered.org/maven/")
}

configurations.getByName("runtimeClasspath").extendsFrom(configurations.getByName("mojangMappedServerRuntime"))
configurations.getByName("runtimeClasspath").extendsFrom(configurations.getByName("mojangMappedServer"))

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())

    implementation(libs.fabric.loader)
}

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    jar {
        archiveClassifier.set("dev")
    }

    shadowJar {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
    }

    reobfJar {
        remapperArgs = TinyRemapper.createArgsList() + "--mixin"
    }
}
