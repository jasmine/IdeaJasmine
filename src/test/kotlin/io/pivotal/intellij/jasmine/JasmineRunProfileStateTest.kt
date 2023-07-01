package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.pivotal.intellij.jasmine.scope.JasmineScope
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import java.io.File

class JasmineRunProfileStateTest : BasePlatformTestCase() {

    private var runExecutor = DefaultRunExecutor()
    private lateinit var configFactory: ConfigurationFactory

    public override fun setUp() {
        super.setUp()

        val baseDir = File(project.basePath)

        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }

        runExecutor = DefaultRunExecutor()
        configFactory = JasmineConfigurationType.getInstance().configurationFactories[0]
    }

    fun `test spec file path in command`() {
        val testFilePath = "/path/to/App.spec.js"

        val testFileCommand = commandLineFromSettings(JasmineRunSettings(
                scope = JasmineScope.SPEC_FILE,
                specFile = testFilePath
        ))
        val suiteCommand = commandLineFromSettings(JasmineRunSettings(
                scope = JasmineScope.SUITE,
                specFile = testFilePath
        ))
        val testCommand = commandLineFromSettings(JasmineRunSettings(
                scope = JasmineScope.TEST,
                specFile = testFilePath
        ))

        assertThat(testFileCommand, endsWith(testFilePath))
        assertThat(suiteCommand, endsWith(testFilePath))
        assertThat(testCommand, endsWith(testFilePath))
    }

    fun `test test names added as filter`() {
        val suiteCommand = commandLineFromSettings(JasmineRunSettings(
                scope = JasmineScope.SUITE,
                testNames = listOf("App", "spec", "name with spaces")
        ))
        val testCommand = commandLineFromSettings(JasmineRunSettings(
                scope = JasmineScope.TEST,
                testNames = listOf("App", "spec", "name with spaces")
        ))

        assertThat(suiteCommand, endsWith("\"--filter=App.spec.name with spaces\""))
        assertThat(testCommand, endsWith("\"--filter=App.spec.name with spaces\""))
    }

    fun `test filter not added when empty test names`() {
        val noFilterCommand = commandLineFromSettings(JasmineRunSettings(
                scope = JasmineScope.TEST,
                testNames = listOf()
        ))

        assertThat(noFilterCommand, not(containsString("--filter")))
    }

    private fun commandLineFromSettings(runSettings: JasmineRunSettings = JasmineRunSettings()): String {
        val config = JasmineRunConfiguration(project, configFactory, "")
        config.jasmineRunSettings = runSettings.copy()

        val execEnvironment = ExecutionEnvironmentBuilder.create(runExecutor, config).build()
        val runState = JasmineRunProfileState(project, config, runExecutor, execEnvironment)
        return (runState.startProcess() as KillableColoredProcessHandler).commandLine
    }
}