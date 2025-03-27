rootProject.name = "monumenta-mixins"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven("https://maven.wagyourtail.xyz/releases")
		maven("https://maven.wagyourtail.xyz/snapshots")
	}
}

includeBuild("unimined-paper")

include("monumenta")
include("plugin-api")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
