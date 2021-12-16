plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.triplet.play") version "3.7.0"
    id("ru.erdenian.shrinkometer")
}

android {
    val minSdkVersion: String by project
    val compileSdkVersion: String by project
    val targetSdkVersion: String by project

    compileSdk = compileSdkVersion.toInt()

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = 18
        versionName = "0.4.5"

        minSdk = minSdkVersion.toInt()
        targetSdk = targetSdkVersion.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "${rootProject.name}-$versionName")
    }

    lint {
        isCheckDependencies = true
        isCheckAllWarnings = true
        xmlReport = false
        isCheckTestSources = true
    }

    buildFeatures.compose = true

    composeOptions {
        val composeVersion: String by project
        kotlinCompilerExtensionVersion = composeVersion
    }

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
        } ?: project.logger.warn("WARNING: Can't create release signing config")
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")

        productFlavors.forEach { flavor ->
            getByName(flavor.name).java.srcDirs("src/${flavor.name}/kotlin")
            "test${flavor.name.capitalize()}".let { getByName(it).java.srcDirs("src/$it/kotlin") }
            "androidTest${flavor.name.capitalize()}".let { getByName(it).java.srcDirs("src/$it/kotlin") }
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
    val appcompatVersion: String by project
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    val activityVersion: String by project
    implementation("androidx.activity:activity-compose:$activityVersion")
    val navigationVersion: String by project
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    val splashscreenVersion: String by project
    implementation("androidx.core:core-splashscreen:$splashscreenVersion")
    // endregion

    // region Core
    val kodeinVersion: String by project
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")
    // endregion

    // region UI
    val keyboardVisibilityEventVersion: String by project
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:$keyboardVisibilityEventVersion")

    val materialVersion: String by project
    implementation("com.google.android.material:material:$materialVersion")
    // endregion
}

play {
    track.set("beta")
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.DRAFT)
    defaultToAppBundles.set(true)
}
