package io.pivotal.intellij.jasmine

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
}