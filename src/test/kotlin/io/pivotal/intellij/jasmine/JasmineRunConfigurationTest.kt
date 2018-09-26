package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

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
}