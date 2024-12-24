pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "Deck"
include(":sample:app")
include(":sample:mainFeature")
include(":core")
include(":sample:subFeatureOne")
include(":sample:subFeatureTwo")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":sample:designsystem")
include(":plugin")
