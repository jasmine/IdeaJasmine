package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.*
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager

/**
 * Program runner for debugging Jasmine tests
 */
class JasmineDebugProgramRunner : GenericProgramRunner<RunnerSettings>() {
    override fun getRunnerId() = "JasmineJavascriptTestRunnerDebug"

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultDebugExecutor.EXECUTOR_ID == executorId && profile is JasmineRunConfiguration
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()
        
        // Execute the state to get the process handler and console
        val result = state.execute(environment.executor, this) ?: return null
        
        // Create a run content descriptor
        val builder = RunContentBuilder(result, environment)
        val descriptor = builder.showRunContent(environment.contentToReuse)
        
        // Register rerun actions
        RerunTestsNotification.showRerunNotification(environment.contentToReuse, result.executionConsole)
        RerunTestsAction.register(descriptor)
        
        return descriptor
    }
}
