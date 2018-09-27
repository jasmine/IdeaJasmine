package io.pivotal.intellij.jasmine.scope

import com.intellij.openapi.project.Project

enum class JasmineScope constructor(val label: String) {
    ALL("&All tests") {
        override fun createView(project: Project): JasmineScopeView {
            return JasmineAllTestsScopeView()
        }
    },
    SPEC_FILE("Spec &file") {
        override fun createView(project: Project): JasmineScopeView {
            return JasmineSpecFileScopeView(project)
        }
    },
    SUITE("S&uite") {
        override fun createView(project: Project): JasmineScopeView {
            return JasmineSuiteScopeView(project)
        }
    },
    TEST("&Test") {
        override fun createView(project: Project): JasmineScopeView {
            return JasmineTestScopeView(project)
        }
    };

    abstract fun createView(project: Project): JasmineScopeView
}
