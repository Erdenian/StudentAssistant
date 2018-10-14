buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    val kotlin_version: String by project

    classpath("com.android.tools.build:gradle:3.2.1")
    classpath(kotlin("gradle-plugin", kotlin_version))
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    maven("https://jitpack.io")
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
