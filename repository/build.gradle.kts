plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
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
        testInstrumentationRunnerArgument(
            "runnerBuilder",
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
        )

        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
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

    packagingOptions {
        // JUnit 5
        exclude("META-INF/LICENSE*")
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
    api(project(":entity"))
    implementation(project(":utils"))
    // endregion

    // region Tests
    val junitVersion: String by project
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    val testRunnerVersion: String by project
    androidTestImplementation("androidx.test:runner:$testRunnerVersion")
    val androidTestVersion: String by project
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:$androidTestVersion")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:$androidTestVersion")
    // endregion

    // region Kotlin
    val kotlinVersion: String by project
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")
    // endregion

    // region AndroidX
    val lifecycleVersion: String by project
    api("androidx.lifecycle:lifecycle-livedata:$lifecycleVersion")

    val roomVersion: String by project
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // endregion

    // region Core
    val kodeinVersion: String by project
    implementation("org.kodein.di:kodein-di-generic-jvm:$kodeinVersion")
    // endregion
}
