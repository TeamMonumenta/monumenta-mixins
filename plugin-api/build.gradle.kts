plugins {
	`java-library`
	`maven-publish`
}

group = properties["group"]!!
version = properties["version"]!!

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
	maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
	api(libs.paper.api)
	api(libs.fabric.loader)
	api(libs.nbtapi.plugin)
}

publishing {
	repositories {
		val url = System.getenv("MAVEN_URL") ?: return@repositories
		maven(url) {
			credentials {
				username = System.getenv("MAVEN_USERNAME")
				password = System.getenv("MAVEN_TOKEN")
			}
		}
	}
}
