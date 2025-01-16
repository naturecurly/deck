package com.naturecurly.deck.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.visitor.AbstractUastVisitor

class ProviderDetector :
    Detector(),
    SourceCodeScanner {

    companion object {
        val ISSUE: Issue = Issue.create(
            "DeckProviderCallReminder",
            "Missing onDeckClear() call",
            "The class extending DeckProvider must call onDeckClear() when destroyed",
            Category.PERFORMANCE,
            6,
            Severity.ERROR,
            Implementation(ProviderDetector::class.java, Scope.JAVA_FILE_SCOPE),
        )

        private const val INTERFACE_QUALIFIED_NAME = "com.naturecurly.deck.DeckProvider"
        private const val METHOD_NAME = "onDeckClear"
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                if (!implementsTargetInterface(node)) {
                    return
                }

                val hasClearCall = checkForClearMethodCall(node)
                if (!hasClearCall) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Class implementing ${INTERFACE_QUALIFIED_NAME.substringAfterLast('.')} must call $METHOD_NAME() method",
                    )
                }
            }
        }
    }

    private fun implementsTargetInterface(clazz: UClass): Boolean {
        return clazz.interfaces.any { it.qualifiedName == INTERFACE_QUALIFIED_NAME }
    }

    private fun checkForClearMethodCall(clazz: UClass): Boolean {
        var foundClearCall = false

        clazz.methods.forEach { method ->
            method.uastBody?.accept(object : AbstractUastVisitor() {
                override fun visitCallExpression(node: UCallExpression): Boolean {
                    if (node.methodName == METHOD_NAME) {
                        foundClearCall = true
                        return true
                    }
                    return super.visitCallExpression(node)
                }
            })
        }

        return foundClearCall
    }
}
