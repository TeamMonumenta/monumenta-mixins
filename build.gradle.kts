plugins {
	id("com.playmonumenta.papermixins.mod-conventions")
}

group = "com.playmonumenta.papermixins"
version = "2.1.0"

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"))

dependencies {
	include(project("plugin-api")) {
		isTransitive = false
	}
	implementation(libs.brigadier)
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

	shadowJar {
		relocate("de.tr7zw.changeme.nbtapi", "de.tr7zw.nbtapi")
	}
}
