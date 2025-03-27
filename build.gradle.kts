import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
	id("com.diffplug.spotless") version "7.0.2" apply false
}

allprojects {
	apply(plugin = "com.diffplug.spotless")
	extensions.getByType(SpotlessExtension::class.java).apply {
		format("misc") {
			target("*.kts")

			leadingSpacesToTabs()
			endWithNewline()
			trimTrailingWhitespace()
		}
	}
}

subprojects {
	extensions.getByType(SpotlessExtension::class.java).apply {
		java {
			importOrder("")
			removeUnusedImports()

			leadingSpacesToTabs()
			endWithNewline()
			trimTrailingWhitespace()
		}
	}
}
