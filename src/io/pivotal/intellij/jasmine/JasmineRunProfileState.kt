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
import com.intellij.openapi.project.Project
import java.io.File

class JasmineRunProfileState(private var project: Project,
                             private var runConfig: JasmineRunConfiguration,
                             private var executor: Executor,
                             environment: ExecutionEnvironment) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val commandLine = GeneralCommandLine()
        commandLine.workDirectory = File(project.baseDir.path)
        val baseDir = File(project.baseDir.path).toPath()
        commandLine.exePath = "/usr/local/bin/node"
        commandLine.addParameter("$baseDir/node_modules/.bin/jasmine")
        commandLine.addParameter("--reporter=$baseDir/spec/support/intellij_reporter.js")

        val processHandler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    override fun createConsole(executor: Executor): ConsoleView? {
        val props = JasmineConsoleProperties(runConfig, this.executor)
        return SMTestRunnerConnectionUtil.createConsole("Jasmine", props)
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
