package io.pivotal.intellij.jasmine.util

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.openapi.util.JDOMUtil
import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.util.JDOMCompare
import io.pivotal.intellij.jasmine.JasmineRunSettings
import io.pivotal.intellij.jasmine.scope.JasmineScope
import org.jdom.Element

class JasmineSerializationUtilTest : LightPlatformTestCase() {

    private lateinit var actualElement: Element
    private lateinit var emptyRunSettings: JasmineRunSettings

    public override fun setUp() {
        super.setUp()

        actualElement = Element("configuration")
        emptyRunSettings = JasmineRunSettings(
                nodeJs = NodeJsInterpreterRef.create(""),
                jasmineExecutable = ""
        )
    }

    fun `test saves all properties from run settings`() {
        val runSettings = emptyRunSettings.copy(
                nodeJs = NodeJsInterpreterRef.create("node"),
                nodeOptions = "--no-warnings",
                workingDir = "\$PROJECT_DIR\$",
                envData = EnvironmentVariablesData.create(mapOf("ENV_NAME" to "ENV_VALUE"), true),
                extraJasmineOptions = "--no-color",
                jasmineExecutable = "jasmine.js",
                jasmineConfigFile = "\$PROJECT_DIR\$/jasmine.json",
                scope = JasmineScope.TEST,
                specFile = "\$PROJECT_DIR\$/spec/App.spec.json",
                testNames = listOf("suite name", "test name")
        )

        JasmineSerializationUtil.writeXml(actualElement, runSettings)

        val expectedElement = JDOMUtil.load("""
            <configuration>
                <node-interpreter value='node' />
                <node-options value='--no-warnings' />
                <working-dir value='${'$'}PROJECT_DIR${'$'}' />
                <jasmine-options value='--no-color' />
                <jasmine-executable value='jasmine.js' />
                <jasmine-config value='${'$'}PROJECT_DIR${'$'}/jasmine.json' />
                <scope value='TEST' />
                <spec-file value='${'$'}PROJECT_DIR${'$'}/spec/App.spec.json' />
                <test-names>
                    <test-name value='suite name' />
                    <test-name value='test name' />
                </test-names>
                <envs>
                    <env name='ENV_NAME' value='ENV_VALUE' />
                </envs>
            </configuration>
        """)

        assertEquals(expectedElement, actualElement)
    }

    fun `test does not save empty properties from run settings`() {
        JasmineSerializationUtil.writeXml(actualElement, emptyRunSettings)

        val expectedElement = JDOMUtil.load("""
            <configuration>
                <scope value='ALL' />
                <envs />
            </configuration>
        """)

        assertEquals(expectedElement, actualElement)
    }

    fun `test saves system independent paths`() {
        val runSettings = emptyRunSettings.copy(
                workingDir = "working\\directory\\path",
                jasmineExecutable = "jasmine\\executable\\path",
                jasmineConfigFile = "jasmine\\config\\path",
                specFile = "test\\file\\path",
                scope = JasmineScope.SPEC_FILE
        )

        JasmineSerializationUtil.writeXml(actualElement, runSettings)

        val expectedElement = JDOMUtil.load("""
            <configuration>
                <working-dir value='working/directory/path' />
                <jasmine-executable value='jasmine/executable/path' />
                <jasmine-config value='jasmine/config/path' />
                <scope value='SPEC_FILE' />
                <spec-file value='test/file/path' />
                <envs />
            </configuration>
        """)

        assertEquals(expectedElement, actualElement)
    }

    private fun assertEquals(expected: Element, actual: Element) {
        assertTrue("Elements not equal: ${JDOMCompare.diffElements(expected, actual)}",
                JDOMUtil.areElementsEqual(expected, actual))
    }

    fun `test reads run settings`() {
        val configElement = JDOMUtil.load("""
            <configuration>
                <node-interpreter value='node' />
                <node-options value='--no-warnings' />
                <working-dir value='${'$'}PROJECT_DIR${'$'}' />
                <jasmine-options value='--no-color' />
                <jasmine-executable value='jasmine.js' />
                <jasmine-config value='${'$'}PROJECT_DIR${'$'}/jasmine.json' />
                <scope value='TEST' />
                <spec-file value='${'$'}PROJECT_DIR${'$'}/spec/App.spec.json' />
                <test-names>
                    <test-name value='suite name' />
                    <test-name value='test name' />
                </test-names>
                <envs>
                    <env name='ENV_NAME' value='ENV_VALUE' />
                </envs>
            </configuration>
        """)

        val actualSettings = JasmineSerializationUtil.readXml(configElement)

        val expectedSettings = emptyRunSettings.copy(
                nodeJs = NodeJsInterpreterRef.create("node"),
                nodeOptions = "--no-warnings",
                workingDir = "\$PROJECT_DIR\$",
                envData = EnvironmentVariablesData.create(mapOf("ENV_NAME" to "ENV_VALUE"), true),
                extraJasmineOptions = "--no-color",
                jasmineExecutable = "jasmine.js",
                jasmineConfigFile = "\$PROJECT_DIR\$/jasmine.json",
                scope = JasmineScope.TEST,
                specFile = "\$PROJECT_DIR\$/spec/App.spec.json",
                testNames = listOf("suite name", "test name")
        )

        assertEquals(expectedSettings, actualSettings)
    }

    fun `test read handles missing properties`() {
        val configElement = JDOMUtil.load("""
            <configuration>
                <scope value='ALL' />
            </configuration>
        """)

        val actualSettings = JasmineSerializationUtil.readXml(configElement)

        assertEquals(emptyRunSettings, actualSettings)
    }
}