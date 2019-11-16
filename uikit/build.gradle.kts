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

        minSdkVersion(21)
        targetSdkVersion(targetSdkVersion.toInt())

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

        productFlavors.forEach { flavor ->
            getByName(flavor.name).java.srcDirs("src/${flavor.name}/kotlin")
            "test${flavor.name.capitalize()}".let { getByName(it).java.srcDirs("src/$it/kotlin") }
            "androidTest${flavor.name.capitalize()}".let { getByName(it).java.srcDirs("src/$it/kotlin") }
        }
    }
}

dependencies {
    val kotlinVersion: String by project

    val appcompatVersion: String by project
    val cardviewVersion: String by project

    implementation(project(":utils"))

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    // endregion

    // region AndroidX
    api("androidx.appcompat:appcompat:$appcompatVersion")
    api("androidx.cardview:cardview:$cardviewVersion")
    api("androidx.recyclerview:recyclerview:1.1.0-rc01")
    // endregion
}
