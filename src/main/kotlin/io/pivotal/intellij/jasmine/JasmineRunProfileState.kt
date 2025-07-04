package io.pivotal.intellij.jasmine

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import io.pivotal.intellij.jasmine.util.JsRegexUtil
import io.pivotal.intellij.jasmine.util.TestNameUtil
import java.io.File
import java.nio.file.Paths

class JasmineRunProfileState(
    private val project: Project,
    private val runConfig: JasmineRunConfiguration,
    private val executor: Executor,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {

    public override fun startProcess(): ProcessHandler {
        val runSettings = runConfig.jasmineRunSettings
        val interpreter = runSettings.nodeJs.resolveAsLocal(project)
        val commandLine = GeneralCommandLine()

        val workingDir = runSettings.workingDir.takeIf { it.isNotBlank() } ?: project.basePath
        commandLine.withWorkDirectory(workingDir)

        commandLine.exePath = interpreter.interpreterSystemDependentPath
        runSettings.envData.configureCommandLine(commandLine, true)

        // Add debug options if we're in debug mode
        val isDebug = executor.id == DefaultDebugExecutor.EXECUTOR_ID
        if (isDebug) {
            // Add Node.js debug options
            commandLine.addParameter("--inspect-brk")
        }
        
        // Add user-specified node options
        val nodeOptionsList = ParametersListUtil.parse(runSettings.nodeOptions.trim())
        commandLine.addParameters(nodeOptionsList)

        commandLine.addParameter(jasminePath(runConfig))

        val jasmineOptionsList = ParametersListUtil.parse(runSettings.extraJasmineOptions.trim())
        commandLine.addParameters(jasmineOptionsList)

        if (runSettings.jasmineConfigFile.isNotBlank()) {
            commandLine.addParameter("--config=${runSettings.jasmineConfigFile}")
        }

        commandLine.addParameter("--reporter=${findReporterPath()}")

        if (runSettings.specFile.isNotBlank()) {
            commandLine.addParameter(runSettings.specFile)
        }

        if (runSettings.testNames.isNotEmpty()) {
            val name = runSettings.testNames.joinToString(" ")
            val escaped = JsRegexUtil.escape(name)
            commandLine.addParameter("--filter=${escaped}")
        }

        val processHandler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    override fun createConsole(executor: Executor): ConsoleView? {
        val props = JasmineConsoleProperties(runConfig, this.executor)
        return SMTestRunnerConnectionUtil.createConsole("Jasmine", props)
    }

    private fun jasminePath(runConfig: JasmineRunConfiguration): String {
        val jasminePath = Paths.get(runConfig.selectedJasminePackage().systemDependentPath)
            .resolve(runConfig.jasmineRunSettings.jasmineExecutable)
        return jasminePath.toAbsolutePath().toString()
    }

    private fun findReporterPath(): String {
        val inputStream = javaClass.classLoader.getResourceAsStream("intellij_reporter.js")
            ?: error("Cannot find intellij_reporter.js in resources")

        val tempFile = File.createTempFile("intellij_reporter", ".js")
        tempFile.deleteOnExit()

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile.absolutePath
    }


    private class JasmineConsoleProperties(
        configuration: JasmineRunConfiguration,
        executor: Executor
    ) : SMTRunnerConsoleProperties(configuration, "Jasmine", executor) {

        init {
            isUsePredefinedMessageFilter = true
            setIfUndefined(HIDE_PASSED_TESTS, false)
            setIfUndefined(HIDE_IGNORED_TEST, true)
            setIfUndefined(SCROLL_TO_SOURCE, true)
            setIfUndefined(SELECT_FIRST_DEFECT, true)
            isIdBasedTestTree = true
            isPrintTestingStartedTime = false
        }
    }
}
