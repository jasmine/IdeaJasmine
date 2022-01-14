package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import icons.JSJasmineIcons

class JasmineConfigurationType : ConfigurationTypeBase("JavascriptTestRunnerJasmine", "Jasmine", "Jasmine", JSJasmineIcons.Jasmine), DumbAware {
    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun getId() = type.id
            override fun createTemplateConfiguration(project: Project) = JasmineRunConfiguration(project, this, "Jasmine")
            override fun getSingletonPolicy() = RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY
        })
    }

    companion object {
        fun getInstance(): JasmineConfigurationType {
            return Holder.INSTANCE
        }
    }


    private object Holder {
        val INSTANCE = ConfigurationTypeUtil.findConfigurationType(JasmineConfigurationType::class.java)
    }
}
