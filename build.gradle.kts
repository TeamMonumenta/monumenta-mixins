plugins {
	id("com.playmonumenta.papermixins.mod-conventions")
}

group = "com.playmonumenta.papermixins"
version = "1.0.4"

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"))

dependencies {
	include(project("plugin-api"))
	include(libs.semver)
	compileOnly(libs.nbtapi.plugin)
	include(libs.nbtapi)
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
