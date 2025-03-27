package com.playmonumenta.uniminedpaper

import org.gradle.api.Plugin
import org.gradle.api.Project

class UniminedPaperPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.plugins.apply("xyz.wagyourtail.unimined")
	}
}
