import io.papermc.paperweight.tasks.TinyRemapper
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.playmonumenta.gradle-config") version "5.3+"
    id("com.playmonumenta.paperweight-aw.userdev") version "2.1.0-build.4+2.0.0-beta.21"
    id("com.gradleup.shadow") version "8.+"
}

group = "com.playmonumenta.papermixins"

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"))

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
        "Jbr25" to javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
            vendor = JvmVendorSpec.JETBRAINS
        },
        "Jdk25" to javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
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

dependencies {
    include(project("plugin-api")) {
        isTransitive = false
    }
    // no-dist impl
    implementation(libs.nbtapi.plugin)
    shade(libs.nbtapi)
}

tasks {
    processResources {
        inputs.properties("version" to version.toString())

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to version.toString()
                )
            )
        }
    }

    reobfJar {
        accessWideners.add("monumenta.accesswidener")
    }

    shadowJar {
        relocate("de.tr7zw.changeme.nbtapi", "de.tr7zw.nbtapi")
    }
}

monumenta {
    name("monumenta-mixins")
    serverConfigSubdir("mods")
    deployArtifactTask("reobfJar")
    disableMaven()
    disableJavadoc()
    overrideJavaVersion()
}

// Mixin classes are processed by the Sponge Mixin framework at runtime — handler methods and their
// required parameters are invisible to static analysis. Disable the checks that fire as false positives.
// Wrapped in afterEvaluate because gradle-config applies Error Prone in its own afterEvaluate.
afterEvaluate {
    tasks.withType<JavaCompile> {
        options.errorprone {
            nullaway {
                severity.set(CheckSeverity.OFF)
            }
            check("UnusedMethod", CheckSeverity.OFF)
            check("UnusedVariable", CheckSeverity.OFF)
            check("InvalidBlockTag", CheckSeverity.OFF)
            check("MissingSummary", CheckSeverity.OFF)
            check("ThreadLocalUsage", CheckSeverity.OFF)
            // Additional checks that produce false positives or low-value warnings in this codebase:
            check("EnumOrdinal", CheckSeverity.OFF)
            check("AnnotateFormatMethod", CheckSeverity.OFF)
            check("FunctionalInterfaceMethodChanged", CheckSeverity.OFF)
            check("TypeParameterUnusedInFormals", CheckSeverity.OFF)
            check("StaticAssignmentOfThrowable", CheckSeverity.OFF)
            check("DoNotCallSuggester", CheckSeverity.OFF)
            check("ThreadPriorityCheck", CheckSeverity.OFF)
        }
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}
