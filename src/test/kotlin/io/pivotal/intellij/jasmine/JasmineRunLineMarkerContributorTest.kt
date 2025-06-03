package io.pivotal.intellij.jasmine

import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.mockito.Mockito
import org.mockito.Mockito.mock

class JasmineRunLineMarkerContributorTest : BasePlatformTestCase() {
    private lateinit var subject: JasmineRunLineMarkerContributor

    public override fun setUp() {
        super.setUp()
        subject = JasmineRunLineMarkerContributor()
    }

    fun `test getInfo returns nil if file is not jS`() {
        val file = mock(PsiFile::class.java) // not a JSFile
        val element = mock(LeafPsiElement::class.java)
        Mockito.`when`(element.containingFile).thenReturn(file)
        assertNull(subject.getInfo(element))
    }

    fun `test getInfo returns non-nil for call to it()`() {
        assertNotNull(subject.getInfo(elementWithJsFileWithCall("it")))
    }

    fun `test getInfo returns non-nil for call to fit()`() {
        assertNotNull(subject.getInfo(elementWithJsFileWithCall("fit")))
    }

    fun `test getInfo returns nil for call to xit()`() {
        assertNull(subject.getInfo(elementWithJsFileWithCall("xit")))
    }

    fun `test getInfo returns non-nil for call to describe()`() {
        assertNotNull(subject.getInfo(elementWithJsFileWithCall("describe")))
    }

    fun `test getInfo returns non-nil for call to fdescribe()`() {
        assertNotNull(subject.getInfo(elementWithJsFileWithCall("fdescribe")))
    }

    fun `test getInfo returns nil for call to xdescribe()`() {
        assertNull(subject.getInfo(elementWithJsFileWithCall("xdescribe")))
    }

    fun `test getInfo returns nil for elements that aren't function calls`() {
        val file = mock(JSFile::class.java)
        val element = mock(LeafPsiElement::class.java)
        Mockito.`when`(element.containingFile).thenReturn(file)
        // Would be valid if the element was a method call
        Mockito.`when`(element.text).thenReturn("describe")
        val parentEl = mock(JSReferenceExpression::class.java)
        Mockito.`when`(element.parent).thenReturn(parentEl)
        // Anything except JSCallExpression
        val grandparentEl = mock(JSExpression::class.java)
        Mockito.`when`(parentEl.parent).thenReturn(grandparentEl)

        assertNull(subject.getInfo(element))
    }

    private fun elementWithJsFileWithCall(methodName: String): PsiElement {
        val file = mock(JSFile::class.java)
        val element = mock(LeafPsiElement::class.java)
        Mockito.`when`(element.containingFile).thenReturn(file)
        Mockito.`when`(element.text).thenReturn(methodName)
        val parentEl = mock(JSReferenceExpression::class.java)
        Mockito.`when`(element.parent).thenReturn(parentEl)
        val grandparentEl = mock(JSCallExpression::class.java)
        Mockito.`when`(parentEl.parent).thenReturn(grandparentEl)
        return element;
    }
}