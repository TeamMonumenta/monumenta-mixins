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
    overrideJavaVersion()
}

dependencies {
	compileOnly(libs.paper.api)
	api(libs.fabric.loader)
	compileOnly(libs.nbtapi.plugin)
}

java {
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}
