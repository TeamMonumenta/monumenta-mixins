import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
    id("com.diffplug.spotless")
}

spotless {
    format("misc") {
        target("*.kts")
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }
    java {
        importOrder("")
        removeUnusedImports()
        indentWithTabs()
        endWithNewline()
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://maven.playmonumenta.com/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "MonumentaMaven"
            url = when (version.toString().endsWith("SNAPSHOT")) {
                true -> uri("https://maven.playmonumenta.com/snapshots")
                false -> uri("https://maven.playmonumenta.com/releases")
            }

            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}