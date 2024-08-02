plugins {
    `java-library`
    id("com.playmonumenta.papermixins.java-conventions")
}

version = rootProject.version

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.nbtapi)
    compileOnly("org.semver4j:semver4j:5.3.0")
}