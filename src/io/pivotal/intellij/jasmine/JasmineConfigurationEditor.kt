package io.pivotal.intellij.jasmine

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


class JasmineConfigurationEditor(project: Project) : SettingsEditor<JasmineRunConfiguration>() {
    private var myNodeInterpreterField: NodeJsInterpreterField = NodeJsInterpreterField(project, false)
    private var myJasminePackageField: NodePackageField = NodePackageField(myNodeInterpreterField, "jasmine")
    private var rootForm: JPanel

    init {
        rootForm = FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Node interpreter", myNodeInterpreterField)
                .addLabeledComponent("Jasmine package", myJasminePackageField)
                .panel
    }

    override fun createEditor(): JComponent = rootForm

    override fun applyEditorTo(config: JasmineRunConfiguration) = Unit

    override fun resetEditorFrom(config: JasmineRunConfiguration) = Unit
}
