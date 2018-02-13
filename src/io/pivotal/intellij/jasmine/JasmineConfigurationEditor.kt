package io.pivotal.intellij.jasmine

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


class JasmineConfigurationEditor(project: Project) : SettingsEditor<JasmineRunConfiguration>() {
    private var nodeJsInterpreterField: NodeJsInterpreterField = NodeJsInterpreterField(project, false)
    private var jasminePackageField: NodePackageField = NodePackageField(nodeJsInterpreterField, "jasmine")
    private var envVars: EnvironmentVariablesComponent = EnvironmentVariablesComponent()
    private var rootForm: JPanel

    init {
        rootForm = FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Node interpreter", nodeJsInterpreterField)
                .addLabeledComponent("Jasmine package", jasminePackageField)
                .addComponent(envVars)
                .panel
    }

    override fun createEditor(): JComponent = rootForm

    override fun applyEditorTo(config: JasmineRunConfiguration) {
        config.nodeJs = nodeJsInterpreterField.interpreterRef
        config.setJasminePackage(jasminePackageField.selected)
        config.envData = envVars.envData
    }

    override fun resetEditorFrom(config: JasmineRunConfiguration) {
        nodeJsInterpreterField.interpreterRef = config.nodeJs
        jasminePackageField.selected = config.selectedJasminePackage()
        if (config.envData != null) {
            envVars.envData = config.envData!!
        }
    }
}
