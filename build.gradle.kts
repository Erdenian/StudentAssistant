import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.gitlab.arturbosch.detekt") version ("1.5.0")
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        val kotlinVersion: String by project

        classpath("com.android.tools.build:gradle:3.5.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.5.2.0")
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
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.5.0")
}
