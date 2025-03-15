plugins {
	`java-library`
	id("com.playmonumenta.papermixins.java-conventions")
}

version = rootProject.version
group = "com.playmonumenta.papermixins"

dependencies {
	compileOnly(libs.paper.api)
	compileOnly(libs.fabric.loader)
	compileOnly(libs.nbtapi.plugin)
	compileOnly("net.kyori:adventure-nbt:4.19.0")
}
