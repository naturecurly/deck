import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.naturecurly.deck.compose"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "com.naturecurly.deck.compose.DeckKt",
                    "com.naturecurly.deck.compose.DeckComposeContainerUi",
                    "com.naturecurly.deck.compose.DeckScope",
                    "com.naturecurly.deck.compose.DeckScopeImpl",
                    "com.naturecurly.deck.compose.di.*",
                    "hilt_aggregated_deps.*",
                )
            }
        }
        verify {
            rule {
                minBound(95, CoverageUnit.LINE)
                minBound(75, CoverageUnit.BRANCH)
            }
        }
    }
}

dependencies {
    api(projects.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.startup.runtime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)

    lintPublish(projects.lint)
}
