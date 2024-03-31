plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.ksp.get().pluginId)
    id(libs.plugins.kover.get().pluginId)

    alias(libs.plugins.tripletPlay)
    alias(libs.plugins.shrinkometer)
}

android {
    namespace = "com.erdenian.studentassistant"

    defaultConfig {
        applicationId = "com.erdenian.studentassistant"
        versionCode = 23
        versionName = "0.6.0"

        resourceConfigurations.retainAll(setOf("ru"))
        base.archivesName = "${rootProject.name}-$versionName"
    }

    // Workaround for: Unable to strip the following libraries, packaging them as they are: libandroidx.graphics.path.so.
    // https://issuetracker.google.com/issues/237187538
    // https://issuetracker.google.com/issues/271316809
    ndkVersion = "26.2.11394342"

    lint {
        checkDependencies = true
        checkAllWarnings = true
        xmlReport = false
        checkTestSources = true
    }

    buildFeatures.compose = true

    signingConfigs {
        val localProperties = File("${rootDir.path}/local.properties").run {
            if (exists()) `java.util`.Properties().apply { load(inputStream()) } else null
        }
        val environment = System.getenv()
        fun get(env: String, local: String) = environment[env] ?: run {
            project.logger.info("No $env environmental variable")
            localProperties?.getProperty(local) ?: run {
                project.logger.info("No $local local property")
                null
            }
        }

        data class Keystore(
            val storeFile: File,
            val storePassword: String,
            val keyAlias: String,
            val keyPassword: String
        )

        fun getReleaseKeystore(): Keystore? {
            return Keystore(
                rootProject.file("signing/release.jks"),
                get("ANDROID_KEYSTORE_PASSWORD", "signing.keystorePassword") ?: return null,
                get("ANDROID_KEY_ALIAS", "signing.keyAlias") ?: return null,
                get("ANDROID_KEY_PASSWORD", "signing.keyPassword") ?: return null
            )
        }

        getByName("debug") {
            storeFile = rootProject.file("signing/debug.jks")
            storePassword = "debugdebug"
            keyAlias = "debug"
            keyPassword = "debugdebug"

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }

        getReleaseKeystore()?.let { keystore ->
            create("release") {
                storeFile = keystore.storeFile
                storePassword = keystore.storePassword
                keyAlias = keystore.keyAlias
                keyPassword = keystore.keyPassword

                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        } ?: project.logger.warn("w: Can't create release signing config")
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }
}

dependencies {
    // region Private
    implementation(project(":core:style"))
    implementation(project(":core:strings"))

    implementation(project(":data:repository"))

    implementation(project(":features:schedule"))
    implementation(project(":features:homeworks"))
    implementation(project(":features:settings"))
    // endregion

    // region AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.core.splashscreen)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)

    // Required in code generated by Dagger
    compileOnly(project(":data:database"))
    compileOnly(libs.androidx.room)
    // endregion
}

dependencies {
    rootProject.subprojects {
        afterEvaluate {
            if (plugins.hasPlugin(libs.plugins.kover.get().pluginId)) kover(project(path))
        }
    }
}

play {
    track.set("beta")
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.DRAFT)
    defaultToAppBundles.set(true)
}

// region Release

rootProject.tasks.register("updateChangelog") {
    val changelogFile = rootProject.file("CHANGELOG.md")
    val newVersion = checkNotNull(android.defaultConfig.versionName)

    doFirst {
        val lines = changelogFile.readLines().toMutableList()
        val lineSeparator = System.lineSeparator()

        val oldVersion = lines
            .first { it.startsWith("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/") }
            .removePrefix("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/")
            .removeSuffix("...develop")

        lines.add(
            lines.indexOf("## [Unreleased]") + 1,
            "$lineSeparator## [$newVersion] - ${`java.time`.LocalDate.now()}"
        )

        lines.set(
            lines.indexOf("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$oldVersion...develop"),
            "[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$newVersion...develop"
        )

        lines.add(
            lines.indexOf("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$newVersion...develop") + 1,
            "[$newVersion]: https://github.com/Erdenian/StudentAssistant/compare/$oldVersion...$newVersion"
        )

        changelogFile.delete()
        changelogFile.writeText(lines.joinToString(lineSeparator) + lineSeparator)
    }
}

// endregion
