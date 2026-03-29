import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway
import org.gradle.api.tasks.compile.JavaCompile

plugins {
	id("com.playmonumenta.papermixins.mod-conventions")
	id("com.playmonumenta.gradle-config") version "3+"
}

group = "com.playmonumenta.papermixins"

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"))

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
	name("monumenta")
	serverConfigSubdir("mods")
	deployArtifactTask("reobfJar")
	disableMaven()
	disableJavadoc()
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
