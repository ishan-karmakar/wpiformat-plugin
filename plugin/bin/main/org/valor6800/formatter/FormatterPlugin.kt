package org.valor6800.formatter

import org.gradle.api.Plugin
import org.gradle.api.Project

class FormatterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("format")
    }
}