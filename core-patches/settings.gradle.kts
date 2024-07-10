pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.floweytf.com/releases")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "monumenta-mixins"
include("api")