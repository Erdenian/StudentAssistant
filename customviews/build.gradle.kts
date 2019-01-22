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

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
    getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
  }
}

dependencies {
  val joda_time_version: String by project
  val guava_version: String by project

  implementation("androidx.appcompat:appcompat:1.0.2")
  implementation("androidx.cardview:cardview:1.0.0")

  implementation(project(":schedule"))

  implementation("joda-time:joda-time:$joda_time_version")
  implementation("com.google.guava:guava:$guava_version")
}
