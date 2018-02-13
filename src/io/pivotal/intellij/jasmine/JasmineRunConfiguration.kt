package io.pivotal.intellij.jasmine

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.project.Project


class JasmineRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) : LocatableConfigurationBase(project, factory, name) {
    var jasmineRunSettings = JasmineRunSettings()
    private var _jasminePackage: NodePackage? = null

    override fun getConfigurationEditor() = JasmineConfigurationEditor(project)

    override fun getState(executor: Executor, environment: ExecutionEnvironment) = JasmineRunProfileState(project, this, executor, environment)

    fun selectedJasminePackage(): NodePackage {
        if (_jasminePackage == null) {
            val interpreter = NodeJsLocalInterpreter.tryCast(jasmineRunSettings.nodeJs.resolve(project))
            val pkg = NodePackage.findPreferredPackage(project, "Jasmine", interpreter)
            _jasminePackage = pkg
            return pkg
        }
        return _jasminePackage!!
    }

    fun setJasminePackage(nodePackage: NodePackage) {
        _jasminePackage = nodePackage
    }
}

