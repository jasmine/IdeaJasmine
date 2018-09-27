package io.pivotal.intellij.jasmine.scope

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import com.intellij.webcore.ui.PathShortener
import io.pivotal.intellij.jasmine.JasmineRunSettings
import javax.swing.JComponent
import javax.swing.JPanel

class JasmineSpecFileScopeView(project: Project) : JasmineScopeView {
    private val specFileField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val panel: JPanel

    init {
        PathShortener.enablePathShortening(specFileField.textField, null)
        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                specFileField,
                "Select Spec File",
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        )
        panel = FormBuilder().setAlignLabelOnRight(false)
                .addLabeledComponent("Spec file:", specFileField)
                .panel
    }

    override fun getComponent(): JComponent {
        return panel
    }

    override fun resetFrom(settings: JasmineRunSettings) {
        specFileField.text = settings.specFile
    }

    override fun applyTo(settings: JasmineRunSettings) {
        settings.specFile = PathShortener.getAbsolutePath(specFileField.textField)
    }
}