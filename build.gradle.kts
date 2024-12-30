import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

allprojects {
    group = "com.naturecurly.deck"
    version = "0.1.0"

    plugins.withId("com.vanniktech.maven.publish.base") {
        configure<MavenPublishBaseExtension> {
            coordinates(group.toString(), "deck-$name", version.toString())
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()

            pom {
                description.set("A Pluggable UI Framework for Compose")
                name.set(project.name)
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