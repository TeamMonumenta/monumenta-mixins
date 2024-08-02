plugins {
    id("com.playmonumenta.papermixins.mod-conventions")
}

group = "com.playmonumenta.papermixins"
version = "1.0.0-SNAPSHOT"

paperweight.awPath.set(file("src/main/resources/monumenta.accesswidener"))

dependencies {
    implementation(project("plugin-api"))
    implementation(libs.semver)
    implementation(libs.nbtapi)

    shadow(project("plugin-api"))
    shadow(libs.semver)
    shadow(libs.nbtapi)
}

val expandTemplates = tasks.register<Copy>("expandTemplates") {
    from("src/main/java-template")
    into(layout.buildDirectory.dir("generated/sources/$name/main/java"))

    eachFile {
        val text = file.readText()
        val updatedText = text.replace("\${version}", version.toString())
        file.writeText(updatedText)
    }
}

tasks {
    processResources {
        inputs.properties("version" to version.toString())

        filesMatching("ignite.mod.json") {
            expand(
                mapOf(
                    "version" to version.toString()
                )
            )
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(expandTemplates)
        }
    }
}