pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        exclusiveContent {
            forRepository {
                maven { url = uri("https://software.mobile.pendo.io/artifactory/androidx-release") }
            }
            filter {
                includeGroup("sdk.pendo.io")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "SimpleCRM"
include(":app")
