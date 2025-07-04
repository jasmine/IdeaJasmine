package io.pivotal.intellij.jasmine.util;

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JsRegexUtilTest : BasePlatformTestCase() {
    // Adapted from https://github.com/sindresorhus/escape-string-regexp/blob/main/test.js
    fun `test escape escapes metacharacters`() {
        assertEquals(
            "\\\\ \\^ \\$ \\* \\+ \\? \\. \\( \\) \\| \\{ \\} \\[ \\]",
            JsRegexUtil.escape("\\ ^ $ * + ? . ( ) | { } [ ]")
        )
    }

    fun `test escape escapes - correctly`() {
        assertEquals("\\x2d", JsRegexUtil.escape("-"))
    }
}
