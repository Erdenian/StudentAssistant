import com.android.build.gradle.internal.api.BaseVariantOutputImpl
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

androidExtensions { isExperimental = true }

tasks.withType<KotlinCompile> {
    kotlinOptions {
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += "-XXLanguage:+InlineClasses"
    }
}

dependencies {
    val kotlin_version: String by project
    val coroutines_version: String by project

    val core_ktx_version: String by project
    val appcompat_version: String by project
    val cardview_version: String by project

    val lifecycle_version: String by project
    val navigation_version: String by project
    val room_version = "2.1.0-beta01"

    val kodein_version = "6.2.0"
    val retrofit_version = "2.5.0"

    val joda_time_version: String by project
    val guava_version: String by project

    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1") {
        exclude("com.android.support", "support-annotations")
    }

    implementation(project(":schedule"))
    implementation(project(":customviews"))

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlin_version))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
    // endregion

    // region AndroidX
    implementation("androidx.core:core-ktx:$core_ktx_version")
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("androidx.cardview:cardview:$cardview_version")

    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycle_version")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("com.shopify:livedata-ktx:3.0.0")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigation_version")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation_version")

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // endregion

    implementation("com.google.android.material:material:1.0.0")

    implementation("org.jetbrains.anko:anko-common:0.10.8")
    implementation("joda-time:joda-time:$joda_time_version")
    implementation("com.google.guava:guava:$guava_version")

    implementation("com.github.ceryle:SegmentedButton:v1.2.2")
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
