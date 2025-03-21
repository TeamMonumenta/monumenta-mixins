plugins {
    kotlin("jvm") version "1.8.21"
    `java-gradle-plugin`
}

group = "com.playmonumenta.uniminedpaper"
version = "2.0.4"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    maven("https://maven.wagyourtail.xyz/releases")
}

dependencies {
    implementation("xyz.wagyourtail.unimined:unimined:1.3.13")
    implementation("xyz.wagyourtail.unimined:unimined:1.3.13")
}

tasks.test {
    useJUnitPlatform()
}