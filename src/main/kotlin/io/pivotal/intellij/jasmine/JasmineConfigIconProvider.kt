package io.pivotal.intellij.jasmine

import com.intellij.ide.IconProvider
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.util.ObjectUtils
import icons.JSJasmineIcons
import io.pivotal.intellij.jasmine.util.JasmineUtil
import javax.swing.Icon

class JasmineConfigIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val jsonFile = ObjectUtils.tryCast(element, JsonFile::class.java) ?: return null

        return when {
            JasmineUtil.isJasmineConfigFile(jsonFile.virtualFile) -> JSJasmineIcons.Jasmine
            else -> null
        }
    }
}