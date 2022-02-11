package ru.erdenian.studentassistant.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import ru.erdenian.studentassistant.lint.desugaring.DesugaringDetector

@Suppress("UnstableApiUsage")
internal class StudentAssistantIssueRegistry : IssueRegistry() {

    override val issues = listOf(DesugaringDetector.ISSUE)

    override val api = CURRENT_API

    override val minApi = 8

    override val vendor: Vendor = Vendor(
        vendorName = "Erdenian",
        feedbackUrl = "https://github.com/Erdenian/StudentAssistant/issues",
        contact = "https://github.com/Erdenian/StudentAssistant"
    )
}
