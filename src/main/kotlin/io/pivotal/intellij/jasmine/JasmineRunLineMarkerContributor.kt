package io.pivotal.intellij.jasmine

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.pivotal.intellij.jasmine.util.JasmineUtil

/**
 * Provides run line markers for Jasmine tests
 * This adds the run/debug actions to the gutter icons
 */
class JasmineRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (!(element.containingFile is JSFile && isRunnableFnCall(element))) {
            return null
        }

        // Get the standard run actions with default run icon
        val actions = ExecutorAction.getActions(0)
        if (actions.isNotEmpty()) {
            return Info(AllIcons.RunConfigurations.TestState.Run, actions, { "Run Jasmine Test" })
        }
        
        return null
    }

    private fun isRunnableFnCall(element: PsiElement): Boolean {
        // Only process actual leaf elements (identifiers)
        if (element !is LeafPsiElement) {
            return false
        }

        val parent = element.parent
        if (parent !is JSReferenceExpression) {
            return false
        }

        val grandParent = parent.parent
        if (grandParent !is JSCallExpression) {
            return false
        }

        return JasmineUtil.runnableFnNames.contains(element.text)
    }
}
