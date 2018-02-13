package io.pivotal.intellij.jasmine

import com.google.common.collect.ImmutableList
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.javascript.testing.JsTestRunConfigurationProducer
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.ObjectUtils.tryCast

class JasmineRunConfigurationProducer : JsTestRunConfigurationProducer<JasmineRunConfiguration>(JasmineConfigurationType.getInstance(), ImmutableList.of("jasmine")) {
    override fun isConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext): Boolean {
        return findTestFile(runConfig, context) != null
    }

    override fun setupConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean {
        val jsFile = findTestFile(runConfig, context) ?: return false
        sourceElement.set(jsFile)
        runConfig.setGeneratedName()

        return true
    }

    private fun findTestFile(runConfig: JasmineRunConfiguration, context: ConfigurationContext): JSFile? {
        val element = context.psiLocation ?: return null
        val currentFile = PsiUtilCore.getVirtualFile(element) ?: return null
        if (currentFile.isDirectory) {
            // not sure the right way to run a dir
            return null
        }

        val psiFile = tryCast(element.containingFile, JSFile::class.java) ?: return null
        psiFile.testFileType ?: return null

        runConfig.jasmineRunSettings = runConfig.jasmineRunSettings.copy(
                specFile = currentFile.path
        )

        return psiFile
    }
}
