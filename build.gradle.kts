plugins {
    id("io.gitlab.arturbosch.detekt") version "1.14.2"
    id("ru.erdenian.shrinkometer") version "0.3.1" apply false
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        val navigationVersion: String by project

        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
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

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                "-Xjvm-default=all"
            )
        }
    }
}

detekt {
    config = files("detekt-config.yml")
    input = files(*subprojects.map { "${it.name}/src" }.toTypedArray())
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.14.2")
}
