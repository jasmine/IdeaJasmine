package io.pivotal.intellij.jasmine

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project


class JasmineRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) : LocatableConfigurationBase(project, factory, name) {
    override fun getConfigurationEditor() = JasmineConfigurationEditor(project)

    override fun getState(executor: Executor, environment: ExecutionEnvironment) = JasmineRunProfileState(project, this, executor, environment)
}

