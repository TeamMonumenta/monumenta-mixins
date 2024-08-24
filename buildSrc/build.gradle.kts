plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenLocal()
    maven("https://maven.playmonumenta.com/releases/")
}

dependencies {
    implementation(libs.gradle.shadow)
    implementation(libs.gradle.paperweight)
    implementation(libs.gradle.spotless)

    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}