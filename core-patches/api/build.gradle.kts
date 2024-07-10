plugins {
    id("java")
    id("maven-publish")
}

group = "com.floweytf.monumentapaper"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paperapi)
}

publishing {
    publications {
        create<MavenPublication>("api") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "FloweyMaven"
            url = uri("https://maven.floweytf.com/releases/")
            credentials {
                password = System.getenv("PASSWORD")
                username = System.getenv("USERNAME")
            }
        }
    }
}