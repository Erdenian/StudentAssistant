plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    val compile_sdk_version: String by project
    val target_sdk_version: String by project

    compileSdkVersion(compile_sdk_version.toInt())

    defaultConfig {
        versionCode = 1
        versionName = "1.0"

        minSdkVersion(15)
        targetSdkVersion(target_sdk_version.toInt())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
    }
}

dependencies {
    val kotlin_version: String by project

    val core_ktx_version: String by project
    val appcompat_version: String by project
    val cardview_version: String by project

    val joda_time_version: String by project

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlin_version))
    // endregion

    // region AndroidX
    implementation("androidx.core:core-ktx:$core_ktx_version")
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("androidx.cardview:cardview:$cardview_version")
    // endregion

    implementation("joda-time:joda-time:$joda_time_version")
}
