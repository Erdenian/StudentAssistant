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

    ":lint"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("config") {
            version("minSdk", "21")
            version("compileSdk", "32")
            version("targetSdk", "32")
        }

        create("libsTest") {
            version("junit", "4.13.2")
            library("junit", "junit", "junit")
                .versionRef("junit")

            version("androidx-junitKtx", "1.1.3")
            library("androidx-junitKtx", "androidx.test.ext", "junit-ktx")
                .versionRef("androidx-junitKtx")

            version("androidx-core", "1.4.0")
            library("androidx-core", "androidx.test", "core-ktx")
                .versionRef("androidx-core")

            version("androidx-runner", "1.4.0")
            library("androidx-runner", "androidx.test", "runner")
                .versionRef("androidx-runner")

            bundle("android", listOf("androidx-junitKtx", "androidx-core", "androidx-runner"))
        }

        create("libsKotlinx") {
            version("coroutines", "1.6.3")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-android")
                .versionRef("coroutines")
        }

        create("libsAndroidTools") {
            version("desugarJdkLibs", "1.1.5")
            library("desugarJdkLibs", "com.android.tools", "desugar_jdk_libs")
                .versionRef("desugarJdkLibs")

            version("lint", "30.2.1")
            library("lint-api", "com.android.tools.lint", "lint-api")
                .versionRef("lint")
            library("lint-checks", "com.android.tools.lint", "lint-checks")
                .versionRef("lint")
            bundle("lint", listOf("lint-api", "lint-checks"))
        }

        create("libsAndroidx") {
            // region Compose
            version("compose-compiler", "1.2.0")
            version("compose", "1.2.0-rc03")

            library("compose-ui", "androidx.compose.ui", "ui")
                .versionRef("compose")
            library("compose-ui-tooling", "androidx.compose.ui", "ui-tooling")
                .versionRef("compose")

            library("compose-foundation", "androidx.compose.foundation", "foundation")
                .versionRef("compose")
            library("compose-material", "androidx.compose.material", "material")
                .versionRef("compose")

            library("compose-material-icons-core", "androidx.compose.material", "material-icons-core")
                .versionRef("compose")
            library("compose-material-icons-extended", "androidx.compose.material", "material-icons-extended")
                .versionRef("compose")

            bundle(
                "compose",
                listOf(
                    "compose-ui",
                    "compose-ui-tooling",
                    "compose-foundation",
                    "compose-material",
                    "compose-material-icons-core",
                    "compose-material-icons-extended"
                )
            )
            // endregion

            version("lifecycle", "2.5.0")
            library("lifecycle-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .versionRef("lifecycle")

            version("room", "2.4.2")
            library("room-compiler", "androidx.room", "room-compiler")
                .versionRef("room")
            library("room", "androidx.room", "room-ktx")
                .versionRef("room")

            version("appcompat", "1.4.2")
            library("appcompat", "androidx.appcompat", "appcompat")
                .versionRef("appcompat")

            version("activity", "1.5.0")
            library("activity", "androidx.activity", "activity-compose")
                .versionRef("activity")

            version("navigation", "2.5.0")
            library("navigation", "androidx.navigation", "navigation-compose")
                .versionRef("navigation")

            version("core-splashscreen", "1.0.0-rc01")
            library("core-splashscreen", "androidx.core", "core-splashscreen")
                .versionRef("core-splashscreen")
        }

        create("libsCore") {
            version("dagger", "2.42")
            library("dagger-compiler", "com.google.dagger", "dagger-compiler")
                .versionRef("dagger")
            library("dagger", "com.google.dagger", "dagger")
                .versionRef("dagger")
        }

        create("libsUi") {
            // Todo: switch to Compose completely to remove this dependency
            version("material", "1.6.1")
            library("material", "com.google.android.material", "material")
                .versionRef("material")

            version("accompanist", "0.24.13-rc")
            library("accompanist-placeholder", "com.google.accompanist", "accompanist-placeholder-material")
                .versionRef("accompanist")
            library("accompanist-pager", "com.google.accompanist", "accompanist-pager")
                .versionRef("accompanist")
            library("accompanist-navigationAnimation", "com.google.accompanist", "accompanist-navigation-animation")
                .versionRef("accompanist")
        }
    }
}
