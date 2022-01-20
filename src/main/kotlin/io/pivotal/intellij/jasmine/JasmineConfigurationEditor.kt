package io.pivotal.intellij.jasmine

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.json.JsonFileType
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.*
import io.pivotal.intellij.jasmine.scope.JasmineScope
import io.pivotal.intellij.jasmine.scope.JasmineScopeView
import io.pivotal.intellij.jasmine.util.JasmineUtil
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import javax.swing.*


class JasmineConfigurationEditor(private var project: Project) : SettingsEditor<JasmineRunConfiguration>() {
    private var nodeJsInterpreterField: NodeJsInterpreterField = NodeJsInterpreterField(project, false)
    private var nodeOptionsField: RawCommandLineEditor = RawCommandLineEditor()
    private var workingDirectoryField = createWorkingDirectoryField()
    private var envVars: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton()
    private var jasminePackageField: NodePackageField = NodePackageField(nodeJsInterpreterField, "jasmine")
    private var jasmineExecutableField = createJasmineExecutableField()
    private var jasmineOptionsField = createJasmineOptionsField()
    private var jasmineConfigFileField = createJasmineConfigFileField()
    private var scopeViewPanel = JPanel(BorderLayout())

    private var scopeButtons = mutableMapOf<JasmineScope, JRadioButton>()
    private var scopeViews = mutableMapOf<JasmineScope, JasmineScopeView>()

    private var longestLabelWidth = JLabel("Environment variables").preferredSize.width

    private var rootForm: JPanel

    init {
        rootForm = FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Node &interpreter", nodeJsInterpreterField)
                .addLabeledComponent("Node &options", nodeOptionsField)
                .addLabeledComponent("&Working directory", workingDirectoryField)
                .addLabeledComponent("&Environment variables", envVars)
                .addLabeledComponent("&Jasmine package", jasminePackageField)
                .addLabeledComponent("Jasmine executable", jasmineExecutableField)
                .addLabeledComponent("Jasmine &config file", jasmineConfigFileField)
                .addLabeledComponent("E&xtra Jasmine options", jasmineOptionsField)
                .addSeparator()
                .addComponent(createScopeRadioButtonPanel())
                .addComponent(scopeViewPanel)
                .panel
    }

    private fun createWorkingDirectoryField(): TextFieldWithBrowseButton {
        val field = TextFieldWithBrowseButton()
        SwingHelper.installFileCompletionAndBrowseDialog(project, field, "Jasmine Working Directory",
                FileChooserDescriptorFactory.createSingleFolderDescriptor())
        return field
    }

    private fun createJasmineOptionsField(): RawCommandLineEditor {
        val editor = RawCommandLineEditor()
        val field = editor.textField
        if (field is ExpandableTextField) {
            field.putClientProperty("monospaced", false)
        }

        if (field is ComponentWithEmptyText) {
            (field as ComponentWithEmptyText).emptyText.text = "CLI options, e.g. --fail-fast=true"
        }

        return editor
    }

    private fun createJasmineExecutableField(): TextFieldWithBrowseButton {
        val fullField = TextFieldWithBrowseButton()
        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                fullField,
                "Select override Jasmine executable file",
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        )

        return fullField
    }

    private fun createJasmineConfigFileField(): TextFieldWithHistoryWithBrowseButton {
        val fullField = TextFieldWithHistoryWithBrowseButton()
        val innerField = fullField.childComponent
        innerField.setHistorySize(-1)
        innerField.setMinimumAndPreferredWidth(0)

        SwingHelper.addHistoryOnExpansion(innerField) {
            innerField.history = emptyList<String>()
            listPossibleConfigFilesInProject().map { file ->
                FileUtil.toSystemDependentName(file.path)
            }.sorted()
        }

        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                fullField,
                "Select override Jasmine configuration file",
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        )

        return fullField
    }

    private fun listPossibleConfigFilesInProject(): List<VirtualFile> {
        val contentScope = ProjectScope.getContentScope(project)
        val scope = contentScope.intersectWith(GlobalSearchScope.notScope(ProjectScope.getLibrariesScope(project)))
        val jsonFileType = JsonFileType.INSTANCE

        val files = FileTypeIndex.getFiles(jsonFileType, scope)

        return files.filter { JasmineUtil.isJasmineConfigFile(it) }
    }

    private fun createScopeRadioButtonPanel(): JPanel {
        val testScopePanel = JPanel(FlowLayout(FlowLayout.CENTER, JBUI.scale(40), 0))
        val buttonGroup = ButtonGroup()

        JasmineScope.values().forEach { scope ->
            val radioButton = JRadioButton(UIUtil.removeMnemonic(scope.label))

            val index = UIUtil.getDisplayMnemonicIndex(scope.label)
            if (index != -1) {
                radioButton.setMnemonic(scope.label[index + 1])
                radioButton.displayedMnemonicIndex = index
            }

            radioButton.addActionListener { setTestScope(scope) }

            scopeButtons[scope] = radioButton
            testScopePanel.add(radioButton)
            buttonGroup.add(radioButton)
        }

        return testScopePanel
    }

    private fun setTestScope(scope: JasmineScope) {
        val scopeView = getScopeView(scope)
        scopeButtons[scope]?.isSelected = true
        scopeViewPanel.removeAll()
        scopeViewPanel.add(scopeView.getComponent(), BorderLayout.CENTER)
        scopeViewPanel.revalidate()
        scopeViewPanel.repaint()
    }

    private fun getScopeView(scope: JasmineScope) = scopeViews.getOrPut(scope) {
        val scopeView = scope.createView(project)

        // align scope view fields with other fields in editor
        scopeView.getComponent().add(
                Box.createHorizontalStrut(longestLabelWidth),
                GridBagConstraints(
                        0, GridBagConstraints.RELATIVE,
                        1, 1,
                        0.0, 0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        JBUI.insetsRight(UIUtil.DEFAULT_HGAP),
                        0, 0
                )
        )

        scopeView
    }

    override fun createEditor(): JComponent = rootForm

    override fun applyEditorTo(config: JasmineRunConfiguration) {
        config.jasmineRunSettings = config.jasmineRunSettings.copy(
                nodeJs = nodeJsInterpreterField.interpreterRef,
                nodeOptions = nodeOptionsField.text,
                workingDir = workingDirectoryField.text,
                envData = envVars.data,
                jasmineExecutable = jasmineExecutableField.text,
                jasmineConfigFile = jasmineConfigFileField.text,
                extraJasmineOptions = jasmineOptionsField.text)

        scopeButtons.entries.find { it.value.isSelected }?.run {
            config.jasmineRunSettings.scope = key
            getScopeView(key).applyTo(config.jasmineRunSettings)
        }

        config.setJasminePackage(jasminePackageField.selected)
    }

    override fun resetEditorFrom(config: JasmineRunConfiguration) {
        val runSettings = config.jasmineRunSettings
        nodeJsInterpreterField.interpreterRef = runSettings.nodeJs
        nodeOptionsField.text = runSettings.nodeOptions
        workingDirectoryField.text = FileUtil.toSystemDependentName(runSettings.workingDir)
        envVars.data = runSettings.envData
        jasminePackageField.selected = config.selectedJasminePackage()
        jasmineExecutableField.text = runSettings.jasmineExecutable
        jasmineConfigFileField.text = runSettings.jasmineConfigFile
        jasmineOptionsField.text = runSettings.extraJasmineOptions
        setTestScope(runSettings.scope)
        getScopeView(runSettings.scope).resetFrom(runSettings)
    }
}
