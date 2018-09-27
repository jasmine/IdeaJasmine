package io.pivotal.intellij.jasmine

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.javascript.testFramework.util.JsTestFqn
import com.intellij.openapi.project.Project
import com.intellij.util.PathUtil
import io.pivotal.intellij.jasmine.scope.JasmineScope
import io.pivotal.intellij.jasmine.util.JasmineSerializationUtil
import org.jdom.Element


class JasmineRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) : LocatableConfigurationBase(project, factory, name) {
    var jasmineRunSettings = JasmineRunSettings()
    private var _jasminePackage: NodePackage? = null

    override fun getConfigurationEditor() = JasmineConfigurationEditor(project)

    override fun getState(executor: Executor, environment: ExecutionEnvironment) = JasmineRunProfileState(project, this, executor, environment)

    fun selectedJasminePackage(): NodePackage {
        if (_jasminePackage == null) {
            val interpreter = NodeJsLocalInterpreter.tryCast(jasmineRunSettings.nodeJs.resolve(project))
            val pkg = NodePackage.findPreferredPackage(project, "jasmine", interpreter)
            _jasminePackage = pkg
            return pkg
        }
        return _jasminePackage!!
    }

    fun setJasminePackage(nodePackage: NodePackage) {
        _jasminePackage = nodePackage
    }

    override fun checkConfiguration() {
        selectedJasminePackage().validateForRunConfiguration("jasmine")
    }

    override fun suggestedName(): String? = when (jasmineRunSettings.scope) {
        JasmineScope.ALL -> "All Tests"
        JasmineScope.SPEC_FILE -> PathUtil.getFileName(jasmineRunSettings.specFile)
        JasmineScope.SUITE, JasmineScope.TEST -> JsTestFqn.getPresentableName(jasmineRunSettings.testNames)
    }

    override fun getActionName(): String? = when (jasmineRunSettings.scope) {
        JasmineScope.SUITE, JasmineScope.TEST -> jasmineRunSettings.testNames.lastOrNull()
        else -> super.getActionName()
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JasmineSerializationUtil.writeXml(element, jasmineRunSettings)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        jasmineRunSettings = JasmineSerializationUtil.readXml(element)
    }
}

