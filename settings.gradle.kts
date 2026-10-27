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
        google()
        mavenCentral()
    }
}

rootProject.name = "NexShizuku"

include(":app")
include(":core:model")
include(":core:designsystem")
include(":core:common")
include(":core:ipc")
include(":service")
include(":feature:home")
include(":feature:service")
include(":feature:permissions")
include(":feature:terminal")
include(":feature:logs")
include(":feature:settings")
