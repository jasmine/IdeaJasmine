package io.pivotal.intellij.jasmine

import com.google.common.collect.ImmutableList
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.javascript.testing.JsTestRunConfigurationProducer
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.ObjectUtils.tryCast

class JasmineRunConfigurationProducer : JsTestRunConfigurationProducer<JasmineRunConfiguration>(JasmineConfigurationType.getInstance(), ImmutableList.of("jasmine")) {
    override fun isConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext): Boolean {
        val element = context.psiLocation ?: return false
        val currentFile = PsiUtilCore.getVirtualFile(element) ?: return false
        if (currentFile.isDirectory) {
            // not sure the right way to run a dir
            return false
        }

        val psiFile = tryCast(element.containingFile, JSFile::class.java) ?: return false
        psiFile.testFileType ?: return false

//        if (element is PsiFileSystemItem) {
            runConfig.jasmineRunSettings = runConfig.jasmineRunSettings.copy(
                    specFile = currentFile.path
            )
//        } else return false

        return true
    }

    override fun setupConfigurationFromCompatibleContext(runConfig: JasmineRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean {
        val element = context.psiLocation ?: return false
        val currentFile = PsiUtilCore.getVirtualFile(element) ?: return false
        if (currentFile.isDirectory) {
            // not sure the right way to run a dir
            return false
        }

        val psiFile = tryCast(element.containingFile, JSFile::class.java) ?: return false

        if (element is PsiFileSystemItem) {
            psiFile.testFileType ?: return false

            runConfig.jasmineRunSettings = runConfig.jasmineRunSettings.copy(
                    specFile = currentFile.path
            )
            sourceElement.set(psiFile)
            runConfig.setGeneratedName()
        } else return false


        return true
    }
}
