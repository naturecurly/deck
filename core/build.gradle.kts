import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kover)
}

kover {
    reports {
        verify {
            rule {
                minBound(95, CoverageUnit.LINE)
                minBound(80, CoverageUnit.BRANCH)
            }
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jakarta.inject)
    implementation(libs.hilt.core)

    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
}
