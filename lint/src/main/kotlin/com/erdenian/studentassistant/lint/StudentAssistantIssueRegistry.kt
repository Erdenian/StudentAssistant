package com.erdenian.studentassistant.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.erdenian.studentassistant.lint.desugaring.DesugaringDetector
import com.intellij.pom.java.LanguageLevel

@Suppress("UnstableApiUsage")
internal class StudentAssistantIssueRegistry : IssueRegistry() {

    override val issues = listOf(DesugaringDetector.ISSUE)

    override val api = CURRENT_API

    override val minApi = LanguageLevel.JDK_1_8.toJavaVersion().feature

    override val vendor: Vendor = Vendor(
        vendorName = "Erdenian",
        feedbackUrl = "https://github.com/Erdenian/StudentAssistant/issues",
        contact = "https://github.com/Erdenian/StudentAssistant"
    )
}
