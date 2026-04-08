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
    maven("https://maven.playmonumenta.com/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.codemc.io/repository/maven-public/")
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

afterEvaluate {
    val mixinJar = configurations.runtimeClasspath.get()
        .resolvedConfiguration
        .resolvedArtifacts
        .filter { artifact -> artifact.moduleVersion.id.module.toString().equals("net.fabricmc:sponge-mixin") }
        .also { assert(it.size == 1) }
        .first()
        .file
        .absolutePath

    val toolchains = mapOf(
        "Jbr17" to javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
            vendor = JvmVendorSpec.JETBRAINS
        },
        "Jbr21" to javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
            vendor = JvmVendorSpec.JETBRAINS
        },
        "Jdk17" to javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
        },
        "Jdk21" to javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }
    )

    fun configureService(postfix: String, toolchain: Provider<JavaLauncher>?) {
        tasks.register<JavaExec>("runServer$postfix") {
            workingDir = project.projectDir.resolve("run")
            classpath = sourceSets.main.get().runtimeClasspath
            mainClass = "com.floweytf.fabricpaperloader.Main"
            group = "runs"
            systemProperties = mapOf(
                "fabric.development" to true,
                "mixin.debug.export" to true,
                "fabric.classPathGroups" to sourceSets.main.get().output.files.joinToString(File.pathSeparator)
            )
            standardInput = System.`in`
            standardOutput = System.out
            errorOutput = System.err
            toolchain?.let { javaLauncher = it }
            jvmArgs(
                "-XX:+AllowEnhancedClassRedefinition",
                "-XX:+IgnoreUnrecognizedVMOptions",
                "-javaagent:$mixinJar",
            )
            args("-nogui")

        }
    }

    toolchains.forEach(::configureService)
    configureService("", null)
}
