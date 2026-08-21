plugins {
	id("com.playmonumenta.gradle-config")
}

repositories {
	maven("https://maven.fabricmc.net/")
}

monumenta {
	name("plugin-api")
	disableJavadoc()
	disableDeploy()
}

dependencies {
	compileOnly(libs.paper.api)
	api(libs.fabric.loader)
	compileOnly(libs.nbtapi.plugin)
}
