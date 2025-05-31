package io.pivotal.intellij.jasmine

/**
 * Program runner for debugging Jasmine tests
 */
class JasmineDebugProgramRunner : JasmineProgramRunner() {
    override fun getRunnerId() = "JasmineJavascriptTestRunnerDebug"
}
