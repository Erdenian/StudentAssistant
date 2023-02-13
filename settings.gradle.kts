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
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("libsPlugins") {
            version("android", "7.4.1")
            plugin("android-application", "com.android.application")
                .versionRef("android")
            plugin("android-library", "com.android.library")
                .versionRef("android")

            version("kotlin", "1.8.10")
            plugin("kotlin-android", "org.jetbrains.kotlin.android")
                .versionRef("kotlin")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm")
                .versionRef("kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.kapt")
                .versionRef("kotlin")
            plugin("kotlin-parcelize", "org.jetbrains.kotlin.plugin.parcelize")
                .versionRef("kotlin")

            version("detekt", "1.22.0")
            plugin("detekt", "io.gitlab.arturbosch.detekt")
                .versionRef("detekt")
            library("detekt-formatting", "io.gitlab.arturbosch.detekt", "detekt-formatting")
                .versionRef("detekt")

            plugin("tripletPlay", "com.github.triplet.play")
                .version("3.8.1")

            plugin("shrinkometer", "ru.erdenian.shrinkometer")
                .version("0.3.1")

            plugin("gradleVersionsFilter", "se.ascp.gradle.gradle-versions-filter")
                .version("0.1.16")
        }

        create("config") {
            version("minSdk", "21")
            version("compileSdk", "33")
            version("targetSdk", "33")
        }

        create("libsTest") {
            library("junit", "junit", "junit")
                .version("4.13.2")

            library("androidx-junitKtx", "androidx.test.ext", "junit-ktx")
                .version("1.1.5")
            library("androidx-core", "androidx.test", "core-ktx")
                .version("1.5.0")
            library("androidx-runner", "androidx.test", "runner")
                .version("1.5.2")

            bundle("android", listOf("androidx-junitKtx", "androidx-core", "androidx-runner"))
        }

        create("libsKotlinx") {
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-android")
                .version("1.6.4")
        }

        create("libsAndroidTools") {
            library("desugarJdkLibs", "com.android.tools", "desugar_jdk_libs")
                .version("2.0.2")
        }

        create("libsAndroidx") {
            // region Compose
            version("compose-compiler", "1.4.2")

            library("compose-bom", "androidx.compose", "compose-bom")
                .version("2023.01.00")

            library("compose-ui", "androidx.compose.ui", "ui")
                .withoutVersion()
            library("compose-ui-tooling", "androidx.compose.ui", "ui-tooling")
                .withoutVersion()

            library("compose-foundation", "androidx.compose.foundation", "foundation")
                .withoutVersion()
            library("compose-material", "androidx.compose.material", "material")
                .withoutVersion()

            library("compose-material-icons-core", "androidx.compose.material", "material-icons-core")
                .withoutVersion()
            library("compose-material-icons-extended", "androidx.compose.material", "material-icons-extended")
                .withoutVersion()

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

            library("lifecycle-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .version("2.5.1")

            version("room", "2.5.0")
            library("room-compiler", "androidx.room", "room-compiler")
                .versionRef("room")
            library("room", "androidx.room", "room-ktx")
                .versionRef("room")

            library("appcompat", "androidx.appcompat", "appcompat")
                .version("1.6.1")

            library("activity", "androidx.activity", "activity-compose")
                .version("1.6.1")

            library("navigation", "androidx.navigation", "navigation-compose")
                .version("2.5.3")

            library("core-splashscreen", "androidx.core", "core-splashscreen")
                .version("1.0.0")
        }

        create("libsCore") {
            version("dagger", "2.45")
            library("dagger-compiler", "com.google.dagger", "dagger-compiler")
                .versionRef("dagger")
            library("dagger", "com.google.dagger", "dagger")
                .versionRef("dagger")
        }

        create("libsUi") {
            // Todo: switch to Compose completely to remove this dependency
            library("material", "com.google.android.material", "material")
                .version("1.8.0")

            version("accompanist", "0.28.0")
            library("accompanist-placeholder", "com.google.accompanist", "accompanist-placeholder-material")
                .versionRef("accompanist")
            library("accompanist-pager", "com.google.accompanist", "accompanist-pager")
                .versionRef("accompanist")
            library("accompanist-navigationAnimation", "com.google.accompanist", "accompanist-navigation-animation")
                .versionRef("accompanist")
        }
    }
}
