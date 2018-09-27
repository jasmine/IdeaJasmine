package io.pivotal.intellij.jasmine.scope

import io.pivotal.intellij.jasmine.JasmineRunSettings
import javax.swing.JComponent
import javax.swing.JPanel

class JasmineAllTestsScopeView : JasmineScopeView {
    override fun getComponent(): JComponent {
        return JPanel()
    }

    override fun resetFrom(settings: JasmineRunSettings) {}

    override fun applyTo(settings: JasmineRunSettings) {}
}