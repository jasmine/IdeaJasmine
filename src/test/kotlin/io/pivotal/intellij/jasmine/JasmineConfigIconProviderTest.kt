package io.pivotal.intellij.jasmine

import com.intellij.json.JsonFileType
import com.intellij.openapi.util.IconLoader.CachedImageIcon
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.hamcrest.CoreMatchers.endsWith
import org.junit.Assert.assertThat

class JasmineConfigIconProviderTest : BasePlatformTestCase() {

    fun `test shows jasmine icon for jasmine config file`() {
        val jasmineConfigFile = myFixture.configureByText("jasmine.json", "")

        val jasmineIcon = JasmineConfigIconProvider().getIcon(jasmineConfigFile, 0) as CachedImageIcon

        assertThat(jasmineIcon.originalPath.toString(), endsWith("/icons/jasmine.png"))
    }

    fun `test does not show jasmine icon on other files`() {
        val jsonFile = myFixture.configureByText(JsonFileType.INSTANCE, "")
        val txtNamedJasmine = myFixture.configureByText("jasmine.txt", "")

        assertNull(JasmineConfigIconProvider().getIcon(jsonFile, 0))
        assertNull(JasmineConfigIconProvider().getIcon(txtNamedJasmine, 0))
    }
}