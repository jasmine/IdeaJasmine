package io.pivotal.intellij.jasmine

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.*
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import io.ktor.util.reflect.instanceOf
import org.jetbrains.annotations.NonNls
import kotlin.reflect.KClass

abstract class JasmineProgramRunner : GenericProgramRunner<RunnerSettings>() {
    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is JasmineRunConfiguration
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()
        val result = state.execute(environment.executor, this) ?: return null
        val builder = RunContentBuilder(result, environment)
        val descriptor = builder.showRunContent(environment.contentToReuse)

        RerunTestsNotification.showRerunNotification(environment.contentToReuse, result.executionConsole)
        RerunTestsAction.register(descriptor)
        return descriptor
    }
}