package io.pivotal.intellij.jasmine

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.json.JsonFileType
import com.intellij.lang.javascript.library.JSLibraryUtil
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
import com.intellij.util.ui.ComponentWithEmptyText
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import javax.swing.JComponent
import javax.swing.JPanel


class JasmineConfigurationEditor(private var project: Project) : SettingsEditor<JasmineRunConfiguration>() {
    private var nodeJsInterpreterField: NodeJsInterpreterField = NodeJsInterpreterField(project, false)
    private var nodeOptionsField: RawCommandLineEditor = RawCommandLineEditor()
    private var workingDirectoryField = createWorkingDirectoryField()
    private var envVars: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton()
    private var jasminePackageField: NodePackageField = NodePackageField(nodeJsInterpreterField, "jasmine")
    private var jasmineOptionsField = createJasmineOptionsField()
    private var jasmineConfigFileField = createJasmineConfigFileField()

    private var rootForm: JPanel

    init {
        nodeOptionsField.dialogCaption = "Node Options"
        rootForm = FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Node &interpreter", nodeJsInterpreterField)
                .addLabeledComponent("Node &options", nodeOptionsField)
                .addLabeledComponent("&Working directory", workingDirectoryField)
                .addLabeledComponent("&Environment variables", envVars)
                .addLabeledComponent("&Jasmine package", jasminePackageField)
                .addLabeledComponent("Jasmine &config file", jasmineConfigFileField)
                .addLabeledComponent("E&xtra Jasmine options", jasmineOptionsField)
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
        editor.dialogCaption = "Extra Jasmine Options"
        val field = editor.textField
        if (field is ExpandableTextField) {
            field.putClientProperty("monospaced", false)
        }

        if (field is ComponentWithEmptyText) {
            (field as ComponentWithEmptyText).emptyText.text = "CLI options, e.g. --fail-fast=true"
        }

        return editor
    }

    private fun createJasmineConfigFileField(): TextFieldWithHistoryWithBrowseButton {
        val fullField = TextFieldWithHistoryWithBrowseButton()
        val innerField = fullField.childComponent
        innerField.setHistorySize(-1)
        innerField.setMinimumAndPreferredWidth(0)

        SwingHelper.addHistoryOnExpansion(innerField) {
            innerField.history = emptyList<String>()
            listPossibleConfigFilesInProject().map {  file ->
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

        return files.filter { it != null && it.isValid && !it.isDirectory && isJasmineConfigFile(it.nameSequence) && !JSLibraryUtil.isProbableLibraryFile(it) }
    }

    private fun isJasmineConfigFile(filename: CharSequence): Boolean {
        return filename.startsWith("jasmine", true)
    }

    override fun createEditor(): JComponent = rootForm

    override fun applyEditorTo(config: JasmineRunConfiguration) {
        config.jasmineRunSettings = config.jasmineRunSettings.copy(
                nodeJs = nodeJsInterpreterField.interpreterRef,
                nodeOptions = nodeOptionsField.text,
                workingDir = workingDirectoryField.text,
                envData = envVars.data,
                jasmineConfigFile = jasmineConfigFileField.text,
                extraJasmineOptions = jasmineOptionsField.text)
        config.setJasminePackage(jasminePackageField.selected)
    }

    override fun resetEditorFrom(config: JasmineRunConfiguration) {
        val runSettings = config.jasmineRunSettings
        nodeJsInterpreterField.interpreterRef = runSettings.nodeJs
        nodeOptionsField.text = runSettings.nodeOptions
        workingDirectoryField.text = FileUtil.toSystemDependentName(runSettings.workingDir)
        envVars.data = runSettings.envData
        jasminePackageField.selected = config.selectedJasminePackage()
        jasmineConfigFileField.text = runSettings.jasmineConfigFile
        jasmineOptionsField.text = runSettings.extraJasmineOptions
    }
}
