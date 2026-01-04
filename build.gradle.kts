@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false

    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/report.xml"))
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
        detektPlugins(rootProject.libs.detekt.rulesCompose)
    }

    detekt {
        parallel = true
        config.setFrom("${project.rootDir}/detekt-config.yml")
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            sarif.required.set(false)
            txt.required.set(false)
            html.required.set(true)
            xml.required.set(true)
        }
        finalizedBy(reportMerge)
    }

    reportMerge { input.from(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().map { it.xmlReportFile }) }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

// region Extensions

fun subprojectsAfterEvaluate(action: Action<in Project>) = subprojects { afterEvaluate(action) }

typealias AndroidExtensions = com.android.build.api.dsl.CommonExtension<*, *, *, *, *, *>

fun Project.configureAndroidIfExists(action: AndroidExtensions.() -> Unit) {
    val androidExtension = extensions.findByName("android") as? AndroidExtensions
    androidExtension?.apply(action)
}

fun AndroidExtensions.ifApplication(
    action: com.android.build.api.dsl.ApplicationExtension.() -> Unit,
) = if (this is com.android.build.api.dsl.ApplicationExtension) action() else Unit

fun AndroidExtensions.ifLibrary(
    action: com.android.build.api.dsl.LibraryExtension.() -> Unit,
) = if (this is com.android.build.api.dsl.LibraryExtension) action() else Unit

// endregion

// region Kotlin

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xannotation-default-target=param-property",

                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
            )
        }
    }
}

subprojects {
    afterEvaluate {
        if (extensions.findByType<JavaPluginExtension>() == null) return@afterEvaluate
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

// endregion

// region Lint

subprojectsAfterEvaluate {
    if (project.plugins.hasPlugin(libs.plugins.kotlin.jvm.get().pluginId)) {
        project.plugins.apply(libs.plugins.android.lint.get().pluginId)
    }
}

// endregion

// region Android

subprojectsAfterEvaluate {
    configureAndroidIfExists {
        ifApplication {
            compileSdk = config.versions.compileSdk.get().toInt()

            defaultConfig {
                minSdk = config.versions.minSdk.get().toInt()
                targetSdk = config.versions.targetSdk.get().toInt()
            }
        }
        ifLibrary {
            compileSdk = config.versions.compileSdk.get().toInt()

            defaultConfig {
                minSdk = config.versions.minSdk.get().toInt()
                lint.targetSdk = config.versions.targetSdk.get().toInt()
                testOptions.targetSdk = config.versions.targetSdk.get().toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                consumerProguardFiles("consumer-rules.pro")
            }
        }

        ifLibrary {
            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = true

            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        packaging {
            resources {
                excludes += "META-INF/LICENSE.md"
                excludes += "META-INF/LICENSE-notice.md"
            }
        }

        testOptions.unitTests.all { it.jvmArgs("--add-opens=java.base/java.time=ALL-UNNAMED") }
        tasks.withType(Test::class) { jvmArgs = listOf("-XX:+EnableDynamicAgentLoading") }

        val androidTestDir = project.file("src/androidTest")
        val androidTestExists = androidTestDir.exists() && androidTestDir.walk().any { it.isFile }

        if (androidTestExists) {
            testOptions.managedDevices.localDevices.create("testDevice") {
                device = "Pixel 4"
                apiLevel = 34
                systemImageSource = "aosp-atd"
                testedAbi = "x86_64"
            }
        }

        project.dependencies {
            if (project.plugins.hasPlugin(libs.plugins.kotlin.compose.get().pluginId)) {
                val bom = platform(libs.androidx.compose.bom)
                "implementation"(bom)
                "androidTestImplementation"(bom)
            }

            configurations.findByName("coreLibraryDesugaring")?.invoke(libs.androidTools.desugarJdkLibs)
        }
    }
}

// endregion
