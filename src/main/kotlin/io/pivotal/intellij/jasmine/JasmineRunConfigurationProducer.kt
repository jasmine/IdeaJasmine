package io.pivotal.intellij.jasmine

import com.google.common.collect.ImmutableList
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.javascript.testing.JsTestRunConfigurationProducer
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSTestFileType
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.util.PsiUtilCore
import io.pivotal.intellij.jasmine.scope.JasmineScope

class JasmineRunConfigurationProducer : JsTestRunConfigurationProducer<JasmineRunConfiguration>(JasmineConfigurationType.getInstance(), ImmutableList.of("jasmine")) {
    override fun isConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext): Boolean {
        val element = context.psiLocation ?: return false

        val thatRunSettings = runConfig.jasmineRunSettings
        val (_, thisRunSettings) = configureSettingsForElement(element, JasmineRunSettings()) ?: return false

        return thisRunSettings.specFile == thatRunSettings.specFile
    }

    override fun setupConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean {
        val element = context.psiLocation ?: return false

        if (!isTestRunnerPackageAvailableFor(element)) {
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
            is PsiFile -> createFileRunSettings(element, elementFile, templateRunSettings)
            else -> null // TODO: All/Suite/Test run settings
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
                } else {
                    return null
                }

        return Pair(element, runSettings)
    }
}
