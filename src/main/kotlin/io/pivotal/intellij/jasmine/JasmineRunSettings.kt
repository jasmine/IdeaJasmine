package io.pivotal.intellij.jasmine

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import io.pivotal.intellij.jasmine.scope.JasmineScope

data class JasmineRunSettings(
    var nodeJs : NodeJsInterpreterRef = NodeJsInterpreterRef.createProjectRef(),
    var nodeOptions : String = "",
    var workingDir : String = "",
    var envData : EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT,
    var extraJasmineOptions : String = "",
    var jasmineExecutable: String = "bin/jasmine.js",
    var jasmineConfigFile : String = "",
    var scope : JasmineScope = JasmineScope.SPEC_FILE,
    var specFile : String = ""
)