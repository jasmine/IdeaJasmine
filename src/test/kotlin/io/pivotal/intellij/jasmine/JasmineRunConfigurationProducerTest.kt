package io.pivotal.intellij.jasmine

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import io.pivotal.intellij.jasmine.scope.JasmineScope
import java.io.File

class JasmineRunConfigurationProducerTest : LightPlatformCodeInsightFixtureTestCase() {

    private lateinit var configProducer: RunConfigurationProducer<JasmineRunConfiguration>
    private lateinit var configFactory: ConfigurationFactory

    public override fun setUp() {
        super.setUp()

        configProducer = RunConfigurationProducer.getInstance(JasmineRunConfigurationProducer::class.java)
        configFactory = JasmineConfigurationType.getInstance().configurationFactories[0]
    }

    override fun getTestDataPath(): String = File("src/test/resources/testData").absolutePath

    fun `test does not setup if jasmine dependency missing`() {
        assertNull(setupRunConfiguration(myFixture.configureByText("App.spec.js", "")))
    }

    fun `test creates all scope configuration from jasmine config file`() {
        myFixture.copyDirectoryToProject("test-project", "")

        val jasmineConfig = myFixture.configureByFile("spec/support/jasmine.json")
        val config = setupRunConfiguration(jasmineConfig)!!

        val runSettings = config.jasmineRunSettings
        assertEquals(JasmineScope.ALL, runSettings.scope)
        assertEquals("/src/spec/support/jasmine.json", runSettings.jasmineConfigFile)
    }

    fun `test does not create all scope configuration from other json files`() {
        myFixture.copyDirectoryToProject("test-project", "")
        assertNull(setupRunConfiguration(myFixture.configureByFile("package.json")))
    }

    fun `test creates file scope configuration from jasmine spec file`() {
        myFixture.copyDirectoryToProject("test-project", "")

        val specFile = myFixture.configureByFile("spec/App.spec.js")
        val config = setupRunConfiguration(specFile)!!

        val runSettings = config.jasmineRunSettings
        assertEquals(JasmineScope.SPEC_FILE, runSettings.scope)
        assertEquals("/src/spec/App.spec.js", runSettings.specFile)
    }

    fun `test does not create file scope configuration from other js files`() {
        myFixture.copyDirectoryToProject("test-project", "")
        assertNull(setupRunConfiguration(myFixture.configureByFile("App.js")))
    }


    fun `test configuration not compatible when context cannot setup configuration`() {
        myFixture.copyDirectoryToProject("test-project", "")

        val jsFileContext = ConfigurationContext(myFixture.configureByFile("App.js"))

        assertConfigNotFromContext(createConfiguration(), jsFileContext)
    }

    fun `test configuration not compatible when context is different scope`() {
        myFixture.copyDirectoryToProject("test-project", "")

        val specFileConfig = createConfiguration(JasmineRunSettings(
                scope = JasmineScope.SPEC_FILE
        ))

        val allScopeContext = ConfigurationContext(myFixture.configureByFile("spec/support/jasmine.json"))

        assertConfigNotFromContext(specFileConfig, allScopeContext)
    }

    fun `test configuration compatible when context is same spec file`() {
        myFixture.copyDirectoryToProject("test-project", "")

        val existingConfig = createConfiguration(JasmineRunSettings(
                specFile = "/src/App.spec.js"
        ))

        val specFile = myFixture.configureByText("App.spec.js", """<caret>describe("suite", function(){})""")
        val specFileContext = ConfigurationContext(specFile)

        assertConfigFromContext(existingConfig, specFileContext)
    }

    private fun setupRunConfigurationAtCaret(specFile: PsiFile): JasmineRunConfiguration? {
        val elementAtCaret = specFile.elementAtCaret()
        return setupRunConfiguration(elementAtCaret)
    }

    private fun setupRunConfiguration(element: PsiElement): JasmineRunConfiguration? {
        val configFromContext = configProducer.createConfigurationFromContext(ConfigurationContext(element))
        val config = configFromContext?.configuration ?: return null
        return config as JasmineRunConfiguration
    }

    private fun PsiFile.elementAtCaret(): PsiElement {
        return this.findElementAt(myFixture.caretOffset)!!
    }

    private fun assertConfigFromContext(config: JasmineRunConfiguration?, context: ConfigurationContext) {
        assertTrue(configProducer.isConfigurationFromContext(config, context))
    }

    private fun assertConfigNotFromContext(config: JasmineRunConfiguration?, context: ConfigurationContext) {
        assertFalse(configProducer.isConfigurationFromContext(config, context))
    }

    private fun createConfiguration(runSettings: JasmineRunSettings = JasmineRunSettings()): JasmineRunConfiguration {
        val config = JasmineRunConfiguration(project, configFactory, "")
        config.jasmineRunSettings = runSettings.copy()
        return config
    }
}