rootProject.name = "StudentAssistant"

include(
    ":app",

    ":features:schedule",
    ":features:homeworks",
    ":features:settings",

    ":common:uikit",
    ":common:utils",
    ":common:sampledata",

    ":data:repository",
    ":data:database",
    ":data:entity",

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
