plugins {
    id("com.android.library")
    kotlin("android")
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

    viewBinding { isEnabled = true }

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

dependencies {
    // region Kotlin
    val kotlinVersion: String by project
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    // endregion

    // region AndroidX
    api("androidx.core:core-ktx:1.2.0")

    api("androidx.fragment:fragment-ktx:1.2.2")

    val lifecycleVersion: String by project
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    api("com.shopify:livedata-ktx:3.0.0")
    // endregion

    // region Core
    val jodaTimeVersion: String by project
    api("joda-time:joda-time:$jodaTimeVersion")
    // endregion
}
