pluginManagement {
	repositories {
		mavenLocal()
		gradlePluginPortal()
		maven("https://maven.playmonumenta.com/releases/")
	}
}

rootProject.name = "monumenta-mixins"

include("plugin-api")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
