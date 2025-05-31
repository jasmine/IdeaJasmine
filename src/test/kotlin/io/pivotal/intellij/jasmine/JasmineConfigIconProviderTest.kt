package io.pivotal.intellij.jasmine

import com.intellij.json.JsonFileType
import com.intellij.openapi.util.IconLoader
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import javax.swing.Icon

class JasmineConfigIconProviderTest : BasePlatformTestCase() {

    fun `test shows jasmine icon for jasmine config file`() {
        val jasmineConfigFile = myFixture.configureByText("jasmine.json", "")

        val expectedIcon: Icon = IconLoader.getIcon("/icons/jasmine.png", JasmineConfigIconProvider::class.java)
        val actualIcon = JasmineConfigIconProvider().getIcon(jasmineConfigFile, 0)

        assertSame("Expected jasmine icon to be used", expectedIcon, actualIcon)
    }

    fun `test does not show jasmine icon on other files`() {
        val jsonFile = myFixture.configureByText(JsonFileType.INSTANCE, "")
        val txtNamedJasmine = myFixture.configureByText("jasmine.txt", "")

        assertNull("No icon expected for generic .json", JasmineConfigIconProvider().getIcon(jsonFile, 0))
        assertNull("No icon expected for .txt", JasmineConfigIconProvider().getIcon(txtNamedJasmine, 0))
    }
}
