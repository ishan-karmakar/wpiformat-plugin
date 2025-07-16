package org.valor6800.formatter

import org.gradle.api.Task

open class FormatterExtension {
    var compileCommandsTask: Task? = null
    var compileCommandsPath: String? = null
    var dirs: Array<String>? = null
}