package com.naturecurly.deck.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.Issue

class CheckRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(ProviderDetector.ISSUE)

    override val vendor: Vendor
        get() =
            Vendor(
                "deck",
                identifier = "deck",
                feedbackUrl = "https://github.com/naturecurly/deck/issues",
            )
}
