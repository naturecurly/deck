import com.diffplug.gradle.spotless.JavaExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.spotless)
}

spotless {
    format("misc") {
        target("*.md", ".gitignore")
        trimTrailingWhitespace()
        leadingTabsToSpaces(2)
        endWithNewline()
    }
    val configureCommonJavaFormat: JavaExtension.() -> Unit = {
        googleJavaFormat(libs.googleJavaFormat.get().version)
    }
    java {
        configureCommonJavaFormat()
        target("**/*.java")
        targetExclude("**/build/**")
    }
    kotlin {
        ktlint(libs.ktlint.get().version)
            .editorConfigOverride(
                mapOf(
                    "ktlint_standard_filename" to "disabled",
                    "ktlint_standard_function-expression-body" to "disabled",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                ),
            )
        target("**/*.kt")
        trimTrailingWhitespace()
        endWithNewline()
        targetExclude("**/Dependencies.kt", "**/build/**")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:backing-property-naming"
        }
    }
    kotlinGradle {
        ktlint(libs.ktlint.get().version)
        target("**/*.gradle.kts")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

allprojects {
    group = "com.naturecurly.deck"
    version = "0.4.0"

    plugins.withId("com.vanniktech.maven.publish.base") {
        configure<MavenPublishBaseExtension> {
            coordinates(group.toString(), "deck-$name", version.toString())
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()

            pom {
                description.set("A Pluggable UI Framework for Compose")
                name.set("deck-${project.name}")
                url.set("https://github.com/naturecurly/deck/")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("https://github.com/naturecurly/deck/")
                    connection.set("scm:git:git://github.com/naturecurly/deck.git")
                    developerConnection.set("scm:git:ssh://git@github.com:naturecurly/deck.git")
                }
                developers {
                    developer {
                        id.set("naturecurly")
                        name.set("NatureCurly")
                    }
                }
            }
        }
    }
}
