import java.io.ByteArrayOutputStream

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-android-extensions")
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

  /*compileOptions {
    setSourceCompatibility(JavaVersion.VERSION_1_8)
    setTargetCompatibility(JavaVersion.VERSION_1_8)
  }*/

  sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
    getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
  }

  /*applicationVariants.all { variant ->
    variant.outputs.all {
      outputFileName = outputFileName.replace('.apk', "-${variant.versionName}.apk")
    }
  }*/
}

dependencies {
  val kotlin_version: String by project
  val support_libraries_version: String by project

  androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2") {
    exclude("com.android.support", "support-annotations")
  }

  implementation(kotlin("stdlib-jdk8", kotlin_version))

  implementation("com.android.support:appcompat-v7:$support_libraries_version")
  implementation("com.android.support:design:$support_libraries_version")
  implementation("com.android.support:cardview-v7:$support_libraries_version")

  implementation(project(":schedule"))
  implementation(project(":customviews"))

  implementation("org.jetbrains.anko:anko-common:0.10.5")
  implementation("joda-time:joda-time:2.10")
  implementation("com.google.guava:guava:26.0-android")

  implementation("com.github.ceryle:SegmentedButton:v1.2.2")
}

fun isMainBranch(): Boolean {
  val out = ByteArrayOutputStream()
  exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
    standardOutput = out
  }
  return out.toString() == "master"
}

fun gitVersionName(): String {
  val out = ByteArrayOutputStream()
  exec {
    commandLine("git", "describe", "--tags")
    standardOutput = out
  }
  return out.toString().trim()
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
