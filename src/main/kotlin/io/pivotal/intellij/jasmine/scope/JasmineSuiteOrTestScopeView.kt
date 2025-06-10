package io.pivotal.intellij.jasmine.scope

import com.intellij.javascript.testFramework.util.TestFullNameView
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import com.intellij.webcore.ui.PathShortener
import io.pivotal.intellij.jasmine.JasmineRunSettings
import javax.swing.JComponent
import javax.swing.JPanel

open class JasmineSuiteOrTestScopeView(project: Project,
                                       testFullNamePopupTitle: String,
                                       testFullNameLabel: String) : JasmineScopeView {
    private val specFileField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val testNameView: TestFullNameView = TestFullNameView(testFullNamePopupTitle)
    private val panel: JPanel

    init {
        PathShortener.enablePathShortening(specFileField.textField, null)
        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                specFileField,
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                    .withTitle("Select Spec File")
        )

        panel = FormBuilder().setAlignLabelOnRight(false)
                .addLabeledComponent("Spec file:", specFileField)
                .addLabeledComponent(testFullNameLabel, testNameView.component)
                .panel
    }

    override fun getComponent(): JComponent {
        return panel
    }

    override fun resetFrom(settings: JasmineRunSettings) {
        specFileField.text = settings.specFile
        testNameView.names = settings.testNames
    }

    override fun applyTo(settings: JasmineRunSettings) {
        settings.specFile = PathShortener.getAbsolutePath(specFileField.textField)
        settings.testNames = testNameView.names
    }
}

class JasmineSuiteScopeView(project: Project) : JasmineSuiteOrTestScopeView(project, "Edit suite name", "Suite name:")
class JasmineTestScopeView(project: Project) : JasmineSuiteOrTestScopeView(project, "Edit test name", "Test name:")