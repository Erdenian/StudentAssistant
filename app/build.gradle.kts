import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsFeature
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    val compile_sdk_version: String by project
    val target_sdk_version: String by project

    compileSdkVersion(compile_sdk_version.toInt())

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = gitVersionCode()
        versionName = gitVersionName()

        minSdkVersion(21)
        targetSdkVersion(target_sdk_version.toInt())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    val app_name: String by project
    applicationVariants.all {
        outputs.forEach { output ->
            output as BaseVariantOutputImpl
            output.apply {
                outputFileName = outputFileName.replace(
                    project.name,
                    "$app_name-${defaultConfig.versionName}"
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
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += "-XXLanguage:+InlineClasses"
    }
}

dependencies {
    val kotlin_version: String by project
    val coroutines_version: String by project

    val lifecycle_version: String by project
    //val navigation_version: String by project
    val room_version: String by project

    //val kodein_version: String by project
    //val retrofit_version: String by project

    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1") {
        exclude("com.android.support", "support-annotations")
    }

    implementation(project(":utils"))
    implementation(project(":customviews"))

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlin_version))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")
    // endregion

    // region AndroidX
    implementation("androidx.fragment:fragment-ktx:1.1.0-beta01")
    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha06")
    implementation("androidx.drawerlayout:drawerlayout:1.1.0-alpha02")
    implementation("androidx.viewpager:viewpager:1.0.0")

    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    //implementation("androidx.navigation:navigation-fragment-ktx:$navigation_version")
    //implementation("androidx.navigation:navigation-ui-ktx:$navigation_version")

    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // endregion

    implementation("org.jetbrains.anko:anko-common:0.10.8")

    // region UI
    implementation("com.google.android.material:material:1.1.0-alpha07")
    implementation("com.github.DavidProdinger:weekdays-selector:1.1.0")
    // endregion
}

fun isMainBranch(mainBranchName: String = "master"): Boolean {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = out
    }
    return out.toString().trim() == mainBranchName
}

fun gitVersionName(): String {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags")
        standardOutput = out
    }
    val version = out.toString().trim()
    return if (!isMainBranch()) version else version.takeWhile { it != '-' }
}

fun gitVersionCode(): Int {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "tag")
        standardOutput = out
    }
    return out.toString().lineSequence().count()
}

tasks.register("printVersion") {
    group = "Other"
    doLast {
        println("Version name: ${gitVersionName()}")
        println("Version code: ${gitVersionCode()}")
    }
}
