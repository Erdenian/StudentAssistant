plugins {
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("ru.erdenian.shrinkometer") version "0.3.1" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
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
            freeCompilerArgs += listOf("-Xjvm-default=all")
        }
    }

    afterEvaluate {
        dependencies {
            configurations.findByName("coreLibraryDesugaring")?.let { coreLibraryDesugaring ->
                coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
            }
        }
    }
}

detekt {
    config = files("detekt-config.yml")
    source = files(*subprojects.map { "${it.projectDir}/src" }.toTypedArray())
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
}
