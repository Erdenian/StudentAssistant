import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsFeature

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("de.mannodermaus.android-junit5")
}

android {
    val compileSdkVersion: String by project
    val targetSdkVersion: String by project

    compileSdkVersion(compileSdkVersion.toInt())

    defaultConfig {
        versionCode = 1
        versionName = "1.0"

        minSdkVersion(16)
        targetSdkVersion(targetSdkVersion.toInt())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
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

androidExtensions {
    features = setOf(AndroidExtensionsFeature.PARCELIZE.featureName)
    isExperimental = true
}

dependencies {
    // region Versions
    val junitVersion: String by project

    val kotlinVersion: String by project
    val roomVersion: String by project
    val jodaTimeVersion: String by project
    // endregion

    // region Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    // endregion

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    // endregion

    // region AndroidX
    implementation("androidx.room:room-ktx:$roomVersion")
    // endregion

    // region Core
    api("joda-time:joda-time:$jodaTimeVersion")
    // endregion
}
