package io.pivotal.intellij.jasmine.util

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.io.FileUtil
import io.pivotal.intellij.jasmine.JasmineRunSettings
import io.pivotal.intellij.jasmine.scope.JasmineScope
import org.jdom.Element

object JasmineSerializationUtil {
    private const val NODE_INTERPRETER = "node-interpreter"
    private const val NODE_OPTIONS = "node-options"
    private const val WORKING_DIR = "working-dir"
    private const val JASMINE_OPTIONS = "jasmine-options"
    private const val JASMINE_EXECUTABLE = "jasmine-executable"
    private const val JASMINE_CONFIG = "jasmine-config"
    private const val SCOPE = "scope"
    private const val SPEC_FILE = "spec-file"
    private const val TEST_NAMES = "test-names"
    private const val TEST_NAME = "test-name"

    fun writeXml(element: Element, runSettings: JasmineRunSettings) {
        element.write(NODE_INTERPRETER, runSettings.nodeJs.referenceName)
        element.write(NODE_OPTIONS, runSettings.nodeOptions)
        element.writePath(WORKING_DIR, runSettings.workingDir)
        element.write(JASMINE_OPTIONS, runSettings.extraJasmineOptions)
        element.writePath(JASMINE_EXECUTABLE, runSettings.jasmineExecutable)
        element.writePath(JASMINE_CONFIG, runSettings.jasmineConfigFile)
        element.write(SCOPE, runSettings.scope.name)
        element.writePath(SPEC_FILE, runSettings.specFile)
        element.writeTestNames(runSettings.testNames)
        runSettings.envData.writeExternal(element)
    }

    private fun Element.write(tagName: String, value: String) {
        if (value.isNotBlank()) {
            JDOMExternalizerUtil.writeCustomField(this, tagName, value)
        }
    }

    private fun Element.writePath(tagName: String, value: String) {
        this.write(tagName, FileUtil.toSystemIndependentName(value))
    }

    private fun Element.writeTestNames(testNames: List<String>) {
        if (testNames.isNotEmpty()) {
            val testNamesElement = Element(TEST_NAMES)
            JDOMExternalizerUtil.addChildrenWithValueAttribute(testNamesElement, TEST_NAME, testNames)
            this.addContent(testNamesElement)
        }
    }

    fun readXml(element: Element): JasmineRunSettings {
        return JasmineRunSettings(
                nodeJs = NodeJsInterpreterRef.create(element.read(NODE_INTERPRETER)),
                nodeOptions = element.read(NODE_OPTIONS),
                workingDir = element.read(WORKING_DIR),
                extraJasmineOptions = element.read(JASMINE_OPTIONS),
                jasmineExecutable = element.read(JASMINE_EXECUTABLE),
                jasmineConfigFile = element.read(JASMINE_CONFIG),
                envData = EnvironmentVariablesData.readExternal(element),
                scope = JasmineScope.valueOf(element.read(SCOPE)),
                specFile = element.read(SPEC_FILE),
                testNames = element.readTestNames()
        )
    }

    private fun Element.read(tagName: String): String {
        return JDOMExternalizerUtil.readCustomField(this, tagName) ?: ""
    }

    private fun Element.readTestNames(): List<String> {
        val testNamesElement = this.getChild(TEST_NAMES) ?: return emptyList()
        return JDOMExternalizerUtil.getChildrenValueAttributes(testNamesElement, TEST_NAME)
    }
}
