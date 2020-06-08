import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.gitlab.arturbosch.detekt") version ("1.9.1")
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        val kotlinVersion: String by project
        val navigationVersion: String by project

        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.6.2.0")
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

subprojects.forEach { module ->
    module.tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                //"-XXLanguage:+InlineClasses",
                "-Xnew-inference"
            )
        }
    }
}

detekt {
    config = files("detekt-config.yml")
    input = files(*subprojects.map { "${it.name}/src" }.toTypedArray())
    reports { xml { enabled = false } }
    failFast = false
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.9.1")
}
