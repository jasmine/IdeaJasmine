package io.pivotal.intellij.jasmine

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.javascript.testFramework.util.JsTestFqn
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.PathUtil
import com.intellij.util.execution.ParametersListUtil
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.file.Paths

class JasmineRunProfileState(private var project: Project,
                             private var runConfig: JasmineRunConfiguration,
                             private var executor: Executor,
                             environment: ExecutionEnvironment) : CommandLineState(environment) {

    public override fun startProcess(): ProcessHandler {
        val runSettings = runConfig.jasmineRunSettings
        val interpreter = runSettings.nodeJs.resolveAsLocal(project)
        val commandLine = GeneralCommandLine()

        if (StringUtils.isBlank(runSettings.workingDir)) {
            commandLine.withWorkDirectory(project.baseDir.path)
        } else {
            commandLine.withWorkDirectory(runSettings.workingDir)
        }

        commandLine.exePath = interpreter.interpreterSystemDependentPath

        runSettings.envData.configureCommandLine(commandLine, true)

        val nodeOptionsList = ParametersListUtil.parse(runSettings.nodeOptions.trim())
        commandLine.addParameters(nodeOptionsList)

        commandLine.addParameter(jasminePath(runConfig))

        val jasmineOptionsList = ParametersListUtil.parse(runSettings.extraJasmineOptions.trim())
        commandLine.addParameters(jasmineOptionsList)

        if (!StringUtils.isBlank(runSettings.jasmineConfigFile)) {
            commandLine.addParameter("--config=${runSettings.jasmineConfigFile}")
        }

        commandLine.addParameter("--reporter=${findReporterPath()}")

        if (runSettings.specFile.isNotBlank()) {
            commandLine.addParameter(runSettings.specFile)
        }

        if (runSettings.testNames.isNotEmpty()) {
            commandLine.addParameter("--filter=${JsTestFqn.getPresentableName(runSettings.testNames)}")
        }

        val processHandler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    override fun createConsole(executor: Executor): ConsoleView? {
        val props = JasmineConsoleProperties(runConfig, this.executor)
        return SMTestRunnerConnectionUtil.createConsole("Jasmine", props)
    }

    private fun jasminePath(runConfig: JasmineRunConfiguration): String{
        val jasminePath = Paths.get(runConfig.selectedJasminePackage().systemDependentPath)
                .resolve(runConfig.jasmineRunSettings.jasmineExecutable)
        return jasminePath.toAbsolutePath().toString()
    }

    private fun findReporterPath(): String{
        val jarPath = File(PathUtil.getJarPathForClass(this.javaClass))
        val pluginRoot = jarPath.parentFile.parentFile
        val reporterPath = FileUtil.toSystemDependentName("lib/intellij_reporter.js")
        return File(pluginRoot, reporterPath).absolutePath
    }

    private class JasmineConsoleProperties(configuration: JasmineRunConfiguration, executor: Executor) : SMTRunnerConsoleProperties(configuration, "Jasmine", executor) {

        init {
            isUsePredefinedMessageFilter = true
            setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
            setIfUndefined(TestConsoleProperties.HIDE_IGNORED_TEST, true)
            setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true)
            setIfUndefined(TestConsoleProperties.SELECT_FIRST_DEFECT, true)
            isIdBasedTestTree = true
            isPrintTestingStartedTime = false
        }
    }

}
