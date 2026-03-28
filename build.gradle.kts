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
