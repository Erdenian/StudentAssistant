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
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libsPlugins") {
            version("android", "7.3.0")
            plugin("android-application", "com.android.application")
                .versionRef("android")
            plugin("android-library", "com.android.library")
                .versionRef("android")

            version("kotlin", "1.7.20")
            plugin("kotlin-android", "org.jetbrains.kotlin.android")
                .versionRef("kotlin")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm")
                .versionRef("kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.kapt")
                .versionRef("kotlin")
            plugin("kotlin-parcelize", "org.jetbrains.kotlin.plugin.parcelize")
                .versionRef("kotlin")

            version("detekt", "1.21.0")
            plugin("detekt", "io.gitlab.arturbosch.detekt")
                .versionRef("detekt")
            library("detekt-formatting", "io.gitlab.arturbosch.detekt", "detekt-formatting")
                .versionRef("detekt")

            plugin("tripletPlay", "com.github.triplet.play")
                .version("3.7.0")

            plugin("shrinkometer", "ru.erdenian.shrinkometer")
                .version("0.3.1")
        }

        create("config") {
            version("minSdk", "21")
            version("compileSdk", "33")
            version("targetSdk", "33")
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
            version("coroutines", "1.6.4")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-android")
                .versionRef("coroutines")
        }

        create("libsAndroidTools") {
            version("desugarJdkLibs", "1.2.0")
            library("desugarJdkLibs", "com.android.tools", "desugar_jdk_libs")
                .versionRef("desugarJdkLibs")
        }

        create("libsAndroidx") {
            // region Compose
            version("compose-compiler", "1.3.2")
            version("compose", "1.2.1")

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

            version("lifecycle", "2.5.1")
            library("lifecycle-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .versionRef("lifecycle")

            version("room", "2.4.3")
            library("room-compiler", "androidx.room", "room-compiler")
                .versionRef("room")
            library("room", "androidx.room", "room-ktx")
                .versionRef("room")

            version("appcompat", "1.5.1")
            library("appcompat", "androidx.appcompat", "appcompat")
                .versionRef("appcompat")

            version("activity", "1.6.0")
            library("activity", "androidx.activity", "activity-compose")
                .versionRef("activity")

            version("navigation", "2.5.2")
            library("navigation", "androidx.navigation", "navigation-compose")
                .versionRef("navigation")

            version("core-splashscreen", "1.0.0")
            library("core-splashscreen", "androidx.core", "core-splashscreen")
                .versionRef("core-splashscreen")
        }

        create("libsCore") {
            version("dagger", "2.44")
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

            version("accompanist", "0.25.1")
            library("accompanist-placeholder", "com.google.accompanist", "accompanist-placeholder-material")
                .versionRef("accompanist")
            library("accompanist-pager", "com.google.accompanist", "accompanist-pager")
                .versionRef("accompanist")
            library("accompanist-navigationAnimation", "com.google.accompanist", "accompanist-navigation-animation")
                .versionRef("accompanist")
        }
    }
}
