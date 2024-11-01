import java.net.URI
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.jetbrains.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {url = uri("https://www.jitpack.io" ) }
        maven {url = uri("https://maven.google.com/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {url = uri("https://www.jitpack.io" ) }
    }
}

rootProject.name = "TestYandexMapKit"
include(":app")
 