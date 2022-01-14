package io.pivotal.intellij.jasmine

import com.google.common.collect.ImmutableList
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.javascript.testFramework.jasmine.JasmineFileStructureBuilder
import com.intellij.javascript.testing.JsTestRunConfigurationProducer
import com.intellij.json.psi.JsonFile
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSTestFileType
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.ObjectUtils
import io.pivotal.intellij.jasmine.scope.JasmineScope
import io.pivotal.intellij.jasmine.util.JasmineUtil

class JasmineRunConfigurationProducer : JsTestRunConfigurationProducer<JasmineRunConfiguration>(ImmutableList.of("jasmine")) {
    override fun getConfigurationFactory(): ConfigurationFactory {
        return JasmineConfigurationType.getInstance().configurationFactories.get(0)
    }

    override fun isConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext): Boolean {
        val element = context.psiLocation ?: return false

        val thatRunSettings = runConfig.jasmineRunSettings
        val (_, thisRunSettings) = configureSettingsForElement(element, JasmineRunSettings()) ?: return false

        if (thisRunSettings.scope != thatRunSettings.scope) {
            return false
        }

        return when (thisRunSettings.scope) {
            JasmineScope.ALL -> true
            JasmineScope.SPEC_FILE -> thisRunSettings.specFile == thatRunSettings.specFile
            JasmineScope.SUITE, JasmineScope.TEST -> {
                thisRunSettings.specFile == thatRunSettings.specFile &&
                        thisRunSettings.testNames == thatRunSettings.testNames
            }
        }
    }

    override fun setupConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean {
        val element = context.psiLocation ?: return false

        if (!isTestRunnerPackageAvailableFor(element, context)) {
            return false
        }

        val (testElement, runSettings) = configureSettingsForElement(element, runConfig.jasmineRunSettings) ?: return false

        runConfig.jasmineRunSettings = runSettings
        sourceElement.set(testElement)
        runConfig.setGeneratedName()
        return true
    }

    private fun configureSettingsForElement(
            element: PsiElement,
            templateRunSettings: JasmineRunSettings
    ): Pair<PsiElement, JasmineRunSettings>? {
        val elementFile = PsiUtilCore.getVirtualFile(element) ?: return null

        return when (element) {
            is PsiFileSystemItem -> createFileRunSettings(element, elementFile, templateRunSettings)
            else -> createSuiteOrTestRunSettings(element, elementFile, templateRunSettings)
        }
    }

    private fun createFileRunSettings(
            element: PsiFileSystemItem,
            elementFile: VirtualFile,
            templateRunSettings: JasmineRunSettings
    ): Pair<PsiElement, JasmineRunSettings>? {

        val runSettings =
                if (element is JSFile && element.testFileType == JSTestFileType.JASMINE) {
                    templateRunSettings.copy(
                            scope = JasmineScope.SPEC_FILE,
                            specFile = elementFile.path
                    )
                } else if (element is JsonFile && JasmineUtil.isJasmineConfigFile(elementFile)) {
                    templateRunSettings.copy(
                            scope = JasmineScope.ALL,
                            jasmineConfigFile = elementFile.path
                    )
                } else {
                    return null
                }

        return Pair(element, runSettings)
    }

    private fun createSuiteOrTestRunSettings(
            element: PsiElement,
            elementFile: VirtualFile,
            templateRunSettings: JasmineRunSettings
    ): Pair<PsiElement, JasmineRunSettings>? {
        val jsFile = ObjectUtils.tryCast(element.containingFile, JSFile::class.java) ?: return null
        val textRange = element.textRange ?: return null

        val jasmineStructure = JasmineFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile)
        val testElementPath = jasmineStructure.findTestElementPath(textRange) ?: return null

        val isSuite = testElementPath.testName == null
        val runTestElementSettings = templateRunSettings.copy(
                scope = if (isSuite) JasmineScope.SUITE else JasmineScope.TEST,
                specFile = elementFile.path,
                testNames = if (isSuite) testElementPath.suiteNames else testElementPath.allNames
        )

        return Pair(testElementPath.testElement, runTestElementSettings)
    }
}
