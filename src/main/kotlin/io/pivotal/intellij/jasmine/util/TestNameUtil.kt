package io.pivotal.intellij.jasmine.util

/**
 * Utility class for handling test names
 */
object TestNameUtil {
    /**
     * Returns a presentable name for a list of test names
     * This replaces the deprecated JsTestFqn.getPresentableName method
     */
    fun getPresentableName(testNames: List<String>): String {
        return testNames.joinToString(".")
    }
}
