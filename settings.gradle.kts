rootProject.name = "StudentAssistant"

include(
    ":app",

    ":features:repository",
    ":features:repository:api",
    ":features:schedule",
    ":features:schedule:api",
    ":features:homeworks",
    ":features:homeworks:api",
    ":features:settings",
    ":features:settings:api",

    ":common:entity",
    ":common:navigation",
    ":common:uikit",
    ":common:utils",
    ":common:sampledata",

    ":core:strings",
    ":core:style",
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("config") { from(files("gradle/config.versions.toml")) }
    }
}
