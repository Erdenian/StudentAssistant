plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    val minSdkVersion: String by project
    val compileSdkVersion: String by project
    val targetSdkVersion: String by project

    compileSdk = compileSdkVersion.toInt()

    defaultConfig {
        minSdk = minSdkVersion.toInt()
        targetSdk = targetSdkVersion.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures.compose = true

    composeOptions {
        val composeVersion: String by project
        kotlinCompilerExtensionVersion = composeVersion
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
    // region Compose
    val composeVersion: String by project

    api("androidx.compose.ui:ui:$composeVersion")
    api("androidx.compose.ui:ui-tooling:$composeVersion")

    api("androidx.compose.foundation:foundation:$composeVersion")
    api("androidx.compose.material:material:$composeVersion")

    api("androidx.compose.material:material-icons-core:$composeVersion")
    api("androidx.compose.material:material-icons-extended:$composeVersion")
    //endregion
}