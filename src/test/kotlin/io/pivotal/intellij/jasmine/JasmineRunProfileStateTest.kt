package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.pivotal.intellij.jasmine.scope.JasmineScope
import java.io.File

class JasmineRunProfileStateTest : BasePlatformTestCase() {

    private lateinit var runExecutor: DefaultRunExecutor
    private lateinit var configFactory: ConfigurationFactory

    override fun setUp() {
        super.setUp()

        File(project.basePath).mkdirs()

        runExecutor = DefaultRunExecutor()
        configFactory = JasmineConfigurationType.getInstance().configurationFactories[0]
    }

    fun `test spec file path in command`() {
        val testFilePath = "/path/to/App.spec.js"

        val testFileCommand = commandLineFromSettings(
            JasmineRunSettings(scope = JasmineScope.SPEC_FILE, specFile = testFilePath)
        )
        val suiteCommand = commandLineFromSettings(
            JasmineRunSettings(scope = JasmineScope.SUITE, specFile = testFilePath)
        )
        val testCommand = commandLineFromSettings(
            JasmineRunSettings(scope = JasmineScope.TEST, specFile = testFilePath)
        )

        assertTrue(testFileCommand.endsWith(testFilePath))
        assertTrue(suiteCommand.endsWith(testFilePath))
        assertTrue(testCommand.endsWith(testFilePath))
    }

    fun `test test names added as filter`() {
        val testNames = listOf("App", "spec", "name with spaces")

        val suiteCommand = commandLineFromSettings(
            JasmineRunSettings(scope = JasmineScope.SUITE, testNames = testNames)
        )
        val testCommand = commandLineFromSettings(
            JasmineRunSettings(scope = JasmineScope.TEST, testNames = testNames)
        )

        assertTrue(suiteCommand.endsWith("\"--filter=App.spec.name with spaces\""))
        assertTrue(testCommand.endsWith("\"--filter=App.spec.name with spaces\""))
    }

    fun `test filter not added when empty test names`() {
        val noFilterCommand = commandLineFromSettings(
            JasmineRunSettings(scope = JasmineScope.TEST, testNames = emptyList())
        )

        assertTrue("--filter" !in noFilterCommand)
    }

    private fun commandLineFromSettings(runSettings: JasmineRunSettings = JasmineRunSettings()): String {
        val config = JasmineRunConfiguration(project, configFactory, "")
        config.jasmineRunSettings = runSettings.copy()

        val execEnvironment = ExecutionEnvironmentBuilder.create(runExecutor, config).build()
        val runState = JasmineRunProfileState(project, config, runExecutor, execEnvironment)
        return (runState.startProcess() as KillableColoredProcessHandler).commandLine
    }
}
