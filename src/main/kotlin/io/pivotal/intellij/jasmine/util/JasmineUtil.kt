package io.pivotal.intellij.jasmine.util

import com.intellij.lang.javascript.library.JSLibraryUtil
import com.intellij.openapi.vfs.VirtualFile

object JasmineUtil {
    fun isJasmineConfigFile(file: VirtualFile?): Boolean {
        return file != null &&
                file.isValid &&
                !file.isDirectory &&
                file.nameSequence.startsWith("jasmine", true) &&
                !JSLibraryUtil.isProbableLibraryFile(file)
    }
}