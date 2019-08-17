import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsFeature
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
    id("de.mannodermaus.android-junit5")
}

android {
    val compile_sdk_version: String by project
    val target_sdk_version: String by project

    compileSdkVersion(compile_sdk_version.toInt())

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = 11
        versionName = "0.3.0"

        minSdkVersion(21)
        targetSdkVersion(target_sdk_version.toInt())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument(
            "runnerBuilder",
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
        )

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
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

    packagingOptions {
        exclude("META-INF/LICENSE*")
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    val app_name: String by project
    applicationVariants.configureEach {
        outputs.forEach { output ->
            (output as BaseVariantOutputImpl).apply {
                outputFileName = outputFileName.replace(
                    project.name, "$app_name-${defaultConfig.versionName}"
                )
            }
        }
    }
}

androidExtensions {
    features = setOf(AndroidExtensionsFeature.PARCELIZE.featureName)
    isExperimental = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += listOf(
            "-XXLanguage:+InlineClasses",
            "-Xnew-inference"
        )
    }
}

dependencies {
    val junit_version = "5.5.1"
    val android_test_version = "1.1.0"

    val kotlin_version: String by project
    val coroutines_version: String by project

    val lifecycle_version: String by project
    val navigation_version: String by project
    val room_version: String by project

    val kodein_version: String by project
    //val retrofit_version: String by project

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")

    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:$android_test_version")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:$android_test_version")

    implementation(project(":utils"))
    implementation(project(":customviews"))

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlin_version))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")
    // endregion

    // region AndroidX
    implementation("androidx.fragment:fragment-ktx:1.2.0-alpha02")
    implementation("androidx.drawerlayout:drawerlayout:1.1.0-alpha03")
    implementation("androidx.viewpager:viewpager:1.0.0")

    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigation_version")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation_version")

    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // endregion

    // region DI
    implementation("org.kodein.di:kodein-di-generic-jvm:$kodein_version")
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodein_version")
    // endregion

    implementation("org.jetbrains.anko:anko-common:0.10.8")

    // region UI
    implementation("com.google.android.material:material:1.1.0-alpha09")
    implementation("com.github.DavidProdinger:weekdays-selector:1.1.0")
    // endregion
}
