plugins {
    id("io.gitlab.arturbosch.detekt") version ("1.1.1")
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        val gradle_plugin_version: String by project
        val kotlin_version: String by project
        val navigation_version: String by project

        classpath("com.android.tools.build:gradle:$gradle_plugin_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigation_version")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.5.1.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

detekt {
    config = files("detekt-config.yml")
    reports { xml { enabled = false } }
    failFast = false

    val sourceSets = listOf("main", "test", "androidTest")
    input = files(*subprojects.flatMap { project ->
        sourceSets.map { "${project.name}/src/$it/kotlin" }
    }.toTypedArray())
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.1.1")
}
