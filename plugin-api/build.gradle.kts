plugins {
	`java-library`
	id("com.playmonumenta.papermixins.java-conventions")
}

version = rootProject.version
group = "com.playmonumenta.papermixins"

dependencies {
	compileOnly(libs.paper.api)
	compileOnly(libs.nbtapi.plugin)
	api(libs.semver)
}
