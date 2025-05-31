package io.pivotal.intellij.jasmine.util

import com.intellij.lang.javascript.psi.JSFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.mockito.Mockito
import org.mockito.Mockito.mock

class JasmineUtilTest : BasePlatformTestCase() {
    fun `test isJasmineTestFile accepts files that call it`() {
        assertTrue(JasmineUtil.isJasmineTestFile(fileWithText("it(")))
    }

    fun `test isJasmineTestFile does not accept non-call references to it`() {
        assertFalse(JasmineUtil.isJasmineTestFile(fileWithText("it")))
    }

    fun `test isJasmineTestFile accepts files that call fit`() {
        assertTrue(JasmineUtil.isJasmineTestFile(fileWithText("fit(")))
    }

    fun `test isJasmineTestFile does not accept non-call references to fit`() {
        assertFalse(JasmineUtil.isJasmineTestFile(fileWithText("fit")))
    }

    fun `test isJasmineTestFile accepts files that call describe`() {
        assertTrue(JasmineUtil.isJasmineTestFile(fileWithText("describe(")))
    }

    fun `test isJasmineTestFile does not accept non-call references to ddescribe`() {
        assertFalse(JasmineUtil.isJasmineTestFile(fileWithText("describe")))
    }

    fun `test isJasmineTestFile accepts files that call fdescribe`() {
        assertTrue(JasmineUtil.isJasmineTestFile(fileWithText("fdescribe(")))
    }

    fun `test isJasmineTestFile does not accept non-call references to fdescribe`() {
        assertFalse(JasmineUtil.isJasmineTestFile(fileWithText("fdescribe")))
    }

    fun `test isJasmineTestFile does not accept the Jest-specifc test fn`() {
        assertFalse(JasmineUtil.isJasmineTestFile(fileWithText("test(")))
    }

    fun `test isJasmineTestFile checks code case-sensitively`() {
        assertFalse(JasmineUtil.isJasmineTestFile(fileWithText("Describe(")))
    }

    private fun fileWithText(text: String): JSFile {
        val file = mock(JSFile::class.java)
        Mockito.`when`(file.text).thenReturn(text)
        return file
    }
}