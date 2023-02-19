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
    ":core:style"
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
        create("config") {
            version("minSdk", "21")
            version("compileSdk", "33")
            version("targetSdk", "33")
        }

        create("libs") {
            // region Plugins
            version("plugins.android", "7.4.1")
            plugin("android-application", "com.android.application")
                .versionRef("plugins.android")
            plugin("android-library", "com.android.library")
                .versionRef("plugins.android")

            version("plugins-kotlin", "1.8.10")
            plugin("kotlin-android", "org.jetbrains.kotlin.android")
                .versionRef("plugins-kotlin")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm")
                .versionRef("plugins-kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.kapt")
                .versionRef("plugins-kotlin")
            plugin("kotlin-parcelize", "org.jetbrains.kotlin.plugin.parcelize")
                .versionRef("plugins-kotlin")

            version("plugins-detekt", "1.22.0")
            plugin("detekt", "io.gitlab.arturbosch.detekt")
                .versionRef("plugins-detekt")
            library("detekt-formatting", "io.gitlab.arturbosch.detekt", "detekt-formatting")
                .versionRef("plugins-detekt")

            plugin("tripletPlay", "com.github.triplet.play")
                .version("3.8.1")

            plugin("shrinkometer", "ru.erdenian.shrinkometer")
                .version("0.3.1")

            plugin("gradleVersionsFilter", "se.ascp.gradle.gradle-versions-filter")
                .version("0.1.16")
            // endregion

            // region Testing
            library("test-junit", "junit", "junit")
                .version("4.13.2")

            library("test-androidx-junitKtx", "androidx.test.ext", "junit-ktx")
                .version("1.1.5")
            library("test-androidx-core", "androidx.test", "core-ktx")
                .version("1.5.0")
            library("test-androidx-runner", "androidx.test", "runner")
                .version("1.5.2")

            bundle("test-android", listOf("test-androidx-junitKtx", "test-androidx-core", "test-androidx-runner"))
            // endregion

            // region KotlinX
            library("kotlinx-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-android")
                .version("1.6.4")
            // endregion

            // region Android Tools
            library("androidTools-desugarJdkLibs", "com.android.tools", "desugar_jdk_libs")
                .version("2.0.2")
            // endregion

            // region Compose
            version("androidx-compose-compiler", "1.4.2")

            library("androidx-compose-bom", "androidx.compose", "compose-bom")
                .version("2023.01.00")

            library("androidx-compose-ui", "androidx.compose.ui", "ui")
                .withoutVersion()
            library("androidx-compose-ui-tooling", "androidx.compose.ui", "ui-tooling")
                .withoutVersion()

            library("androidx-compose-foundation", "androidx.compose.foundation", "foundation")
                .withoutVersion()
            library("androidx-compose-material", "androidx.compose.material", "material")
                .withoutVersion()

            library("androidx-compose-material-icons-core", "androidx.compose.material", "material-icons-core")
                .withoutVersion()
            library("androidx-compose-material-icons-extended", "androidx.compose.material", "material-icons-extended")
                .withoutVersion()

            bundle(
                "androidx-compose",
                listOf(
                    "androidx-compose-ui",
                    "androidx-compose-ui-tooling",
                    "androidx-compose-foundation",
                    "androidx-compose-material",
                    "androidx-compose-material-icons-core",
                    "androidx-compose-material-icons-extended"
                )
            )
            // endregion

            // region AndroidX
            library("androidx-lifecycle-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .version("2.5.1")

            version("androidx-room", "2.5.0")
            library("androidx-room-compiler", "androidx.room", "room-compiler")
                .versionRef("androidx-room")
            library("androidx-room", "androidx.room", "room-ktx")
                .versionRef("androidx-room")

            library("androidx-appcompat", "androidx.appcompat", "appcompat")
                .version("1.6.1")

            library("androidx-activity", "androidx.activity", "activity-compose")
                .version("1.6.1")

            library("androidx-navigation", "androidx.navigation", "navigation-compose")
                .version("2.5.3")

            library("androidx-core-splashscreen", "androidx.core", "core-splashscreen")
                .version("1.0.0")
            // endregion

            // region Core
            version("core-dagger", "2.45")
            library("core-dagger-compiler", "com.google.dagger", "dagger-compiler")
                .versionRef("core-dagger")
            library("core-dagger", "com.google.dagger", "dagger")
                .versionRef("core-dagger")
            // endregion

            // region UI
            // Todo: switch to Compose completely to remove this dependency
            library("ui-material", "com.google.android.material", "material")
                .version("1.8.0")

            version("ui-accompanist", "0.28.0")
            library("ui-accompanist-placeholder", "com.google.accompanist", "accompanist-placeholder-material")
                .versionRef("ui-accompanist")
            library("ui-accompanist-pager", "com.google.accompanist", "accompanist-pager")
                .versionRef("ui-accompanist")
            library("ui-accompanist-navigationAnimation", "com.google.accompanist", "accompanist-navigation-animation")
                .versionRef("ui-accompanist")
            // endregion
        }
    }
}
