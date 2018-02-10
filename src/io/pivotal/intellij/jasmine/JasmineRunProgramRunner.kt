package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RerunTestsAction
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager

object JasmineRunProgramRunner : GenericProgramRunner<RunnerSettings>() {
    override fun getRunnerId() = "JasmineJavascriptTestRunnerRun"

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is JasmineRunConfiguration
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()
        val result = state.execute(environment.executor, this) ?: return null
        val builder = RunContentBuilder(result, environment)
        val descriptor = builder.showRunContent(environment.contentToReuse)

        RerunTestsAction.register(descriptor)
        return descriptor
    }
}