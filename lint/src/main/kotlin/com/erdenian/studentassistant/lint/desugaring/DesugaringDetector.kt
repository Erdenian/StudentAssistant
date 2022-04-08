package com.erdenian.studentassistant.lint.desugaring

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.getQualifiedName
import org.jetbrains.uast.tryResolveNamed
import org.jetbrains.uast.util.isConstructorCall
import org.jetbrains.uast.util.isMethodCall

@Suppress("UnstableApiUsage")
internal class DesugaringDetector : Detector(), UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UQualifiedReferenceExpression::class.java,
        UCallExpression::class.java
    )

    override fun createUastHandler(context: JavaContext): UElementHandler = object : UElementHandler() {

        private val classes = readDesugaringClasses(checkNotNull(javaClass.getResource("/desugaring_apis.txt")))

        private val ignoredFunctions = listOf("let")

        override fun visitCallExpression(node: UCallExpression) {
            if (node.isConstructorCall().not()) return

            val className = node.classReference?.getQualifiedName() ?: return
            val classMembers = classes[className] ?: return
            val parameters = node.valueArguments.map { checkNotNull(it.getExpressionType()?.canonicalText) }

            val message =
                if (classMembers.constructors.contains(parameters)) return
                else "This code calls an unavailable constructor $className(${parameters.joinToString()})"
            context.report(ISSUE, node, context.getLocation(node), message)
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            val receiverName = node.receiver.getExpressionType()?.canonicalText
                ?: (node.receiver as? UReferenceExpression)?.getQualifiedName()
                ?: return

            when (val selector = node.selector) {
                is UCallExpression -> {
                    if (selector.isMethodCall().not()) return

                    val instanceName = selector.receiverType?.canonicalText
                    val isStatic = (instanceName == null)
                    val className = instanceName ?: receiverName
                    val classMembers = classes[className] ?: return

                    val returnType = checkNotNull(selector.getExpressionType()?.canonicalText)
                    val methodName = checkNotNull(selector.methodName)
                    if (methodName in ignoredFunctions) return
                    val parameters = selector.valueArguments.map { checkNotNull(it.getExpressionType()?.canonicalText) }

                    val message =
                        if (classMembers.methods.contains(isStatic, returnType, methodName, parameters)) return
                        else "This code calls an unavailable method $className.$methodName(${parameters.joinToString()})"
                    context.report(ISSUE, selector, context.getLocation(node), message)
                }
                is UReferenceExpression -> {
                    val classMembers = classes[receiverName] ?: return
                    val selectorName = checkNotNull(node.selector.tryResolveNamed()?.name)
                    val returnType = node.selector.getExpressionType()?.canonicalText.toString()

                    val message =
                        if (
                            classMembers.fields.contains(true, receiverName, selectorName) ||
                            classMembers.methods.contains(false, returnType, selectorName, emptyList())
                        ) return
                        else "This code accesses an unavailable field $receiverName.$selectorName})"
                    context.report(ISSUE, selector, context.getLocation(node), message)
                }
            }
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            enabledByDefault = false,
            id = "Desugaring",
            briefDescription = "API not available through desugaring",
            explanation = """
                This API is not available through desugaring.
            """,
            moreInfo = "https://developer.android.com/studio/write/java8-support-table",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = Implementation(
                DesugaringDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
