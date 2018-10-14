plugins {
  id("com.android.library")
}

android {
  val compile_sdk_version: String by project
  val target_sdk_version: String by project
  val build_tools_version: String by project

  compileSdkVersion(compile_sdk_version.toInt())
  buildToolsVersion(build_tools_version)

  defaultConfig {
    versionCode = 1
    versionName = "1.0"

    minSdkVersion(14)
    targetSdkVersion(target_sdk_version.toInt())

    testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

    vectorDrawables.useSupportLibrary = true
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
}

dependencies {
  val kotlin_version: String by project
  val support_libraries_version: String by project

  implementation("com.android.support:appcompat-v7:$support_libraries_version")
  implementation("com.android.support:cardview-v7:$support_libraries_version")

  implementation(project(":schedule"))

  implementation("joda-time:joda-time:2.10")
  implementation("com.google.guava:guava:26.0-android")
}
