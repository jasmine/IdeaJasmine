package io.pivotal.intellij.jasmine.util

import com.intellij.lang.javascript.library.JSLibraryUtil
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
     * This function is here to serve as an aid in troubleshooting during development. It can be used to
     * display information of interest at various points of execution.
     */
    fun notify(message: String) {
        val notification = Notification("Jasmine Plugin", "Jasmine", message, NotificationType.INFORMATION)
        Notifications.Bus.notify(notification)
    }
}