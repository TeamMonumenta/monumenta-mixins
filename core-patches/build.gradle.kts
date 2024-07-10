plugins {
    `java-library`
    id("com.floweytf.paperweight-aw.userdev") version "1.1.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.ajoberstar.grgit") version "5.2.2"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://maven.floweytf.com/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

val shadowImplementation: Configuration by configurations.creating

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"));

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.get().replace("userdev-", ""))

    // We need ignite!
    implementation(libs.bundles.ignite)

    // We need paper!
    implementation(libs.paper)

    // Required for server to start when running "Minecraft Server"
    implementation(libs.bundles.papermisc)

    compileOnly(libs.bundles.mixin)

    implementation(project(":api"))
    shadowImplementation(project(":api"))
    // Tiny remapper
    remapper(libs.tinyremapper) {
        artifact {
            classifier = "fat"
        }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
}

tasks {
    jar {
        archiveClassifier.set("dev")

        manifest {
            attributes["Git-Branch"] = grgit.branch.current().name
            attributes["Git-Hash"] = grgit.log().first().id
        }
    }

    shadowJar {
        archiveClassifier.set("")
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