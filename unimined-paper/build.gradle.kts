plugins {
	kotlin("jvm") version "2.1.0"
	id("com.diffplug.spotless") version "7.0.2"
	`java-gradle-plugin`
	`maven-publish`
}

group = "com.playmonumenta.uniminedpaper"
version = "1.0.1"

spotless {
	format("misc") {
		target("*.kts")
		leadingSpacesToTabs()
		endWithNewline()
		trimTrailingWhitespace()
	}
	kotlin {
		leadingSpacesToTabs()
		endWithNewline()
		trimTrailingWhitespace()
	}
}

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
	maven("https://maven.wagyourtail.xyz/snapshots")
}

dependencies {
	implementation("xyz.wagyourtail.unimined:unimined:1.4.0-SNAPSHOT")
	implementation("xyz.wagyourtail.unimined.mapping:unimined-mapping-library-jvm:1.0.0-SNAPSHOT")
	implementation("io.sigpipe:jbsdiff:1.0") {
		exclude(group = "org.apache.commons")
	}
	implementation("com.google.code.gson:gson:2.12.1")
}

gradlePlugin {
	plugins {
		create("simplePlugin") {
			id = "com.playmonumenta.uniminedpaper"
			implementationClass = "com.playmonumenta.uniminedpaper.UniminedPaperPlugin"
		}
	}
}

publishing {
	repositories {
		val url = System.getenv("MAVEN_URL") ?: return@repositories
		maven(url) {
			credentials {
				username = System.getenv("MAVEN_USERNAME")
				password = System.getenv("MAVEN_TOKEN")
			}
		}
	}
}
