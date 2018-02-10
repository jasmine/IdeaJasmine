package io.pivotal.intellij.jasmine;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;

class JasmineRunConfigurationProducer : RunConfigurationProducer<JasmineRunConfiguration>(JasmineConfigurationType.getInstance()) {

    override fun setupConfigurationFromContext(runConfig: JasmineRunConfiguration,
                                               config: ConfigurationContext,
                                               sourceElement: Ref<PsiElement>) = true

    override fun isConfigurationFromContext(p0: JasmineRunConfiguration?,
                                            p1: ConfigurationContext?) = true
}
