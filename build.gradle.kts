plugins {
	id("com.playmonumenta.papermixins.mod-conventions")
}

group = "com.playmonumenta.papermixins"
version = "1.0.2"

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"))

dependencies {
	implementation(project("plugin-api"))

	shadowImplementation(project("plugin-api"))
	shadowImplementation(libs.semver)
	shadowImplementation(libs.nbtapi)
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
