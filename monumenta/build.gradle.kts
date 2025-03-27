import com.playmonumenta.uniminedpaper.paper

plugins {
	`java-library`
	id("com.playmonumenta.uniminedpaper") version "1.0.1"
}

group = properties["group"]!!
version = properties["version"]!!

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
	maven("https://repo.codemc.io/repository/maven-public/")
}

unimined {
	minecraft {
		version("1.20.4")
		side("server")

		merged {
			paper(449)

			// ok this isn't actually real fabric :P
			fabric {
				loader("0.16.10")
				@Suppress("UnstableApiUsage")
				customIntermediaries = true
				prodNamespace("paperProd")
				accessWidener("monumenta/src/main/resources/monumenta.accesswidener")
			}
		}

		mappings {
			mojmap()
			devNamespace("mojmap")

			stubs("paperProd", "official", "mojmap") {
				c("net/minecraft/world/entity/monster/EntityZombieVillager") {
					m("b;(I)V", "stubbedBrokenMethod;(I)V", "stubbedBrokenMethod;(I)V")
				}
				c("net/minecraft/world/entity/monster/EntityZombie") {
					m("b;(I)V", "stubbedBrokenMethod;(I)V", "stubbedBrokenMethod;(I)V")
				}
			}
		}

		// no refmaps!
		defaultRemapJar = false
		remap(tasks.jar.get()) {
			prodNamespace("paperProd")
			mixinRemap {
				disableRefmap()
			}
		}
	}
}

dependencies {
	implementation(libs.mixinextras)
	implementation(project(":plugin-api"))

	// jij these things
	"include"(project(":plugin-api"))
	"include"(libs.nbtapi.plugin)
	"include"(libs.mixinextras)
}

tasks {
	assemble {
		dependsOn("remapJar")
	}

	jar {
		archiveClassifier = "dev"
	}

	processResources {
		inputs.properties("version" to version.toString())
		filesMatching("fabric.mod.json") {
			expand(mapOf("version" to version.toString()))
		}
	}
}

