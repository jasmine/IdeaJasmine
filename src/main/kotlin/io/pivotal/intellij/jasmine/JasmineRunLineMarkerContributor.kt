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
        // Only process elements in Jasmine test files
        val file = element.containingFile
        if (file !is JSFile || !JasmineUtil.isJasmineTestFile(file)) {
            return null
        }
        
        // Only process actual leaf elements (identifiers)
        if (element !is LeafPsiElement) {
            return null
        }
        
        // Check if this is a describe or it identifier
        val text = element.text
        if (text != "describe" && text != "it" && text != "fdescribe" && text != "fit") {
            return null
        }
        
        // Check if parent is a reference expression
        val parent = element.parent
        if (parent !is JSReferenceExpression) {
            return null
        }
        
        // Check if grandparent is a call expression
        val grandParent = parent.parent
        if (grandParent !is JSCallExpression) {
            return null
        }
        
        // Get the standard run actions with default run icon
        val actions = ExecutorAction.getActions(0)
        if (actions.isNotEmpty()) {
            // Use AllIcons.RunConfigurations.TestState.Run for the default run icon
            return Info(AllIcons.RunConfigurations.TestState.Run, actions, { "Run Jasmine Test" })
        }
        
        return null
    }
}
