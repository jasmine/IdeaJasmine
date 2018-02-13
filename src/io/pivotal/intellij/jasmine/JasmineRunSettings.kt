package io.pivotal.intellij.jasmine

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef

data class JasmineRunSettings(
    var nodeJs : NodeJsInterpreterRef = NodeJsInterpreterRef.createProjectRef(),
    var nodeOptions : String = "",
    var workingDir : String = "",
    var envData : EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT,
    var extraJasmineOptions : String = "",
    var jasmineConfigFile : String = "",
    var specFile : String = ""
)