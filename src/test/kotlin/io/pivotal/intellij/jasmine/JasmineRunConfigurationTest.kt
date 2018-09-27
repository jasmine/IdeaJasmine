package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import io.pivotal.intellij.jasmine.scope.JasmineScope

class JasmineRunConfigurationTest : LightPlatformCodeInsightFixtureTestCase() {

    private lateinit var subject: JasmineRunConfiguration

    public override fun setUp() {
        super.setUp()

        val configFactory = JasmineConfigurationType.getInstance().configurationFactories[0]
        subject = configFactory.createTemplateConfiguration(project) as JasmineRunConfiguration
    }

    fun `test resolves jasmine package`() {
        myFixture.addFileToProject("node_modules/jasmine/package.json", "")

        assertEquals("/src/node_modules/jasmine", subject.selectedJasminePackage().systemIndependentPath)
    }

    fun `test configuration error when jasmine package not set`() {
        try {
            subject.checkConfiguration()
            fail("should throw RuntimeConfigurationError")
        } catch (re: RuntimeConfigurationError) {
            assertEquals("Unspecified jasmine package", re.message)
        }
    }

    fun `test suggested and action name is 'All Tests' for all scope`() {
        subject.jasmineRunSettings = JasmineRunSettings(scope = JasmineScope.ALL)
        subject.setGeneratedName()

        assertEquals("All Tests", subject.suggestedName())
        assertEquals("All Tests", subject.actionName)
    }

    fun `test suggested and action name is file name for file scope`() {
        subject.jasmineRunSettings = JasmineRunSettings(
                scope = JasmineScope.SPEC_FILE,
                specFile = "spec/App.spec.js"
        )
        subject.setGeneratedName()

        assertEquals("App.spec.js", subject.suggestedName())
        assertEquals("App.spec.js", subject.actionName)
    }

    fun `test suggested name is full test name for suite scope`() {
        subject.jasmineRunSettings = JasmineRunSettings(
                scope = JasmineScope.SUITE,
                testNames = listOf("top suite", "nested suite")
        )

        assertEquals("top suite.nested suite", subject.suggestedName())
    }

    fun `test suggested name is full test name for test scope`() {
        subject.jasmineRunSettings = JasmineRunSettings(
                scope = JasmineScope.TEST,
                testNames = listOf("suite name", "test name")
        )

        assertEquals("suite name.test name", subject.suggestedName())
    }

    fun `test action name is suite name for suite scope`() {
        subject.jasmineRunSettings = JasmineRunSettings(
                scope = JasmineScope.SUITE,
                testNames = listOf("top suite", "nested suite")
        )
        subject.setGeneratedName()

        assertEquals("nested suite", subject.actionName)
    }

    fun `test action name is test name for test scope`() {
        subject.jasmineRunSettings = JasmineRunSettings(
                scope = JasmineScope.TEST,
                testNames = listOf("suite name", "test name")
        )
        subject.setGeneratedName()

        assertEquals("test name", subject.actionName)
    }

    fun `test action name is null for empty test names`() {
        subject.jasmineRunSettings = JasmineRunSettings(scope = JasmineScope.TEST)
        subject.setGeneratedName()

        assertNull(subject.actionName)
    }
}