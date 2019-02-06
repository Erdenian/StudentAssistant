import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("org.jetbrains.kotlin.kapt")
}

android {
    val compile_sdk_version: String by project
    val target_sdk_version: String by project
    val build_tools_version: String by project

    compileSdkVersion(compile_sdk_version.toInt())
    buildToolsVersion(build_tools_version)

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = gitVersionCode()
        versionName = gitVersionName()

        minSdkVersion(21)
        targetSdkVersion(target_sdk_version.toInt())

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
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
    android.applicationVariants.all {
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

dependencies {
    val room_version = "2.1.0-alpha03"

    val kotlin_version: String by project
    val joda_time_version: String by project
    val guava_version: String by project

    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.1.1") {
        exclude("com.android.support", "support-annotations")
    }

    implementation(kotlin("stdlib-jdk8", kotlin_version))

    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation(project(":schedule"))
    implementation(project(":customviews"))

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-coroutines:$room_version")

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
