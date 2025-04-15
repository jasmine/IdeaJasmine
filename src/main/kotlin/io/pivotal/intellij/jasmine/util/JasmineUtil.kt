package io.pivotal.intellij.jasmine.util

import com.intellij.lang.javascript.library.JSLibraryUtil
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.vfs.VirtualFile

object JasmineUtil {
    fun isJasmineConfigFile(file: VirtualFile?): Boolean {
        return file != null &&
                file.isValid &&
                !file.isDirectory &&
                file.nameSequence.startsWith("jasmine", true) &&
                !JSLibraryUtil.isProbableLibraryFile(file)
    }

    /**
     * Checks if a JavaScript file is a Jasmine test file by analyzing content for test constructs.
     * This is meant to be used as a fast check before analyzing the AST.
     */
    fun isJasmineTestFile(jsFile: JSFile): Boolean {
        val fileContent = jsFile.text.lowercase()
        return fileContent.contains("jasmine.") || (fileContent.contains("describe(") &&
                (fileContent.contains("it(") || fileContent.contains("test(")))
    }

    /**
     * This function is here to serve as an aid in troubleshooting during development. It can be used to
     * display information of interest at various points of execution.
     */
    fun notify(message: String) {
        val notification = Notification("Jasmine Plugin", "Jasmine", message, NotificationType.INFORMATION)
        Notifications.Bus.notify(notification)
    }
}