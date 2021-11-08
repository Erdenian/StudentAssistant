plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    id("de.mannodermaus.android-junit5")
}

android {
    val compileSdkVersion: String by project
    val targetSdkVersion: String by project

    compileSdk = compileSdkVersion.toInt()

    defaultConfig {
        minSdk = 16
        targetSdk = targetSdkVersion.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"

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
        resources.excludes += "META-INF/LICENSE*"
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
    api(project(":data:entity"))
    // endregion

    // region Tests
    val junitVersion: String by project
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    val androidxTestVersion: String by project
    androidTestImplementation("androidx.test:core:$androidxTestVersion")
    androidTestImplementation("androidx.test:runner:$androidxTestVersion")
    val junit5AndroidTestVersion: String by project
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:$junit5AndroidTestVersion")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:$junit5AndroidTestVersion")
    // endregion

    // region Kotlin
    val coroutinesVersion: String by project
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    // endregion

    // region AndroidX
    val roomVersion: String by project
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // endregion

    // region Core
    val jodaTimeVersion: String by project
    api("joda-time:joda-time:$jodaTimeVersion")

    val kodeinVersion: String by project
    implementation("org.kodein.di:kodein-di-jvm:$kodeinVersion")
    // endregion
}
