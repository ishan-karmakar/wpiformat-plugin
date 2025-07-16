package org.valor6800.formatter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.process.ExecOperations
import javax.inject.Inject

interface InjectedExecOps {
    @get:Inject
    val execOps: ExecOperations
}

class FormatterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.add("formatter", FormatterExtension::class.java)
        project.tasks.register("format") { task ->
            val ext = project.extensions.getByType(FormatterExtension::class.java)
            if (ext.compileCommandsTask != null)
                task.dependsOn(ext.compileCommandsTask)
            task.group = "Formatting"
            task.description = "Formats the target directories with wpiformat, IN PLACE"
            val injected = project.objects.newInstance(InjectedExecOps::class.java)

            task.doLast {
                injected.execOps.exec {
                    it.commandLine = wpiformatCmdline(ext)
                }
            }
        }

        project.tasks.register("lint") { task ->
            val ext = project.extensions.getByType(FormatterExtension::class.java)
            if (ext.compileCommandsTask != null)
                task.dependsOn(ext.compileCommandsTask)
            task.group = "Formatting"
            task.description = "Formats the target directories with wpiformat, but RESTORES CHANGES"
            val injected = project.objects.newInstance(InjectedExecOps::class.java)

            task.doLast {
                injected.execOps.exec { it.commandLine = listOf("git", "add", "-u") }
                injected.execOps.exec {
                    it.commandLine = listOf("git", "commit", "--allow-empty", "-m", "\"TMP: Formatting changes\"")
                }
                injected.execOps.exec {
                    it.commandLine = wpiformatCmdline(ext)
                    it.isIgnoreExitValue = true
                }
                injected.execOps.exec { it.commandLine = listOf("git", "reset", "--hard", "HEAD") }
                injected.execOps.exec { it.commandLine = listOf("git", "reset", "HEAD~1") }
            }
        }
    }

    fun wpiformatCmdline(ext: FormatterExtension): List<String> {
        return listOf(
            "wpiformat",
            "-f",
            *ext.dirs!!,
            "-compile-commands",
            ext.compileCommandsPath!!
        )
    }
}
