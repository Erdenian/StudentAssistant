plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false

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

fun Project.configureAndroidIfExists(action: com.android.build.gradle.BaseExtension.() -> Unit) {
    if (extensions.findByType<com.android.build.gradle.BaseExtension>() != null) extensions.configure(action)
}

fun com.android.build.gradle.BaseExtension.ifApplication(
    action: com.android.build.gradle.internal.dsl.BaseAppModuleExtension.() -> Unit,
) = if (this is com.android.build.gradle.internal.dsl.BaseAppModuleExtension) action() else Unit

fun com.android.build.gradle.BaseExtension.ifLibrary(
    action: com.android.build.gradle.LibraryExtension.() -> Unit,
) = if (this is com.android.build.gradle.LibraryExtension) action() else Unit

// endregion

// region Kotlin

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",

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

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

        testOptions.managedDevices.devices.create<com.android.build.api.dsl.ManagedVirtualDevice>("testDevice") {
            device = "Pixel 4"
            apiLevel = 34
            systemImageSource = "aosp"
            testedAbi = "x86_64"
        }

        dependencies {
            if (project.plugins.hasPlugin(libs.plugins.kotlin.compose.get().pluginId)) {
                "implementation"(platform(libs.androidx.compose.bom))
            }

            configurations.findByName("coreLibraryDesugaring")?.invoke(libs.androidTools.desugarJdkLibs)
        }
    }
}

subprojectsAfterEvaluate {
    configureAndroidIfExists {
        fun com.android.build.gradle.api.AndroidSourceSet?.hasFiles() = this
            ?.java
            ?.srcDirs
            ?.single()
            ?.absoluteFile
            ?.parentFile
            ?.walk()
            ?.any { it.isFile } == true

        val unitTestExists = sourceSets.findByName("test").hasFiles()
        val androidTestExists = sourceSets.findByName("androidTest").hasFiles()

        if (!androidTestExists) testOptions.managedDevices.devices.clear()
    }
}

// endregion
