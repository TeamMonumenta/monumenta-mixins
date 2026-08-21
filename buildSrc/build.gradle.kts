plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.playmonumenta.com/releases/")
}

dependencies {
    implementation(libs.gradle.paperweight)
    implementation(libs.gradle.shadow)
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
