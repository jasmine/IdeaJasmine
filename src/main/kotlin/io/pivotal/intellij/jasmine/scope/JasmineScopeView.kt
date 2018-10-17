package io.pivotal.intellij.jasmine.scope

import io.pivotal.intellij.jasmine.JasmineRunSettings
import javax.swing.JComponent

interface JasmineScopeView {
    fun getComponent(): JComponent
    fun resetFrom(settings: JasmineRunSettings)
    fun applyTo(settings: JasmineRunSettings)
}
