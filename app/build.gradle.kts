plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.github.triplet.play") version "3.5.0"
    id("ru.erdenian.shrinkometer")
}

android {
    val compileSdkVersion: String by project
    val targetSdkVersion: String by project

    compileSdkVersion(compileSdkVersion.toInt())

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = 18
        versionName = "0.4.5"

        minSdkVersion(21)
        targetSdkVersion(targetSdkVersion.toInt())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "${rootProject.name}-$versionName")
    }

    buildFeatures.viewBinding = true

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
    implementation(project(":repository"))
    implementation(project(":uikit"))
    implementation(project(":utils"))
    // endregion

    // region AndroidX
    val navigationVersion: String by project
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.viewpager:viewpager:1.0.0")
    // endregion

    // region Core
    val kodeinVersion: String by project
    implementation("org.kodein.di:kodein-di-jvm:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")
    // endregion

    // region UI
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC3")
    implementation("com.github.DavidProdinger:weekdays-selector:1.1.1")
    // endregion
}

play {
    track.set("beta")
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.DRAFT)
    defaultToAppBundles.set(true)
}
