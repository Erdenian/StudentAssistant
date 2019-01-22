plugins {
    id("io.gitlab.arturbosch.detekt") version ("1.0.0-RC12")
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        val kotlin_version: String by project

        classpath("com.android.tools.build:gradle:3.3.0")
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

detekt {
    config = files("detekt-config.yml")
    input = files(*subprojects.map { "${it.name}/src/main/kotlin" }.toTypedArray())
    reports { xml { enabled = false } }
}
