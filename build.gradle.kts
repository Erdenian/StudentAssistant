plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false

    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)

    alias(libs.plugins.gradleVersionsFilter)
}

detekt {
    config.setFrom("detekt-config.yml")
    source.setFrom(files(*subprojects.map { "${it.projectDir}/src" }.toTypedArray()))
}

dependencies {
    detektPlugins(libs.detekt.formatting)
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
    action: com.android.build.gradle.internal.dsl.BaseAppModuleExtension.() -> Unit
) = if (this is com.android.build.gradle.internal.dsl.BaseAppModuleExtension) action() else Unit

fun com.android.build.gradle.BaseExtension.ifLibrary(
    action: com.android.build.gradle.LibraryExtension.() -> Unit
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
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
        }
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

        composeOptions.kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()

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
        }

        dependencies {
            if (buildFeatures.compose == true) "implementation"(platform(libs.androidx.compose.bom))

            configurations.findByName("coreLibraryDesugaring")?.invoke(libs.androidTools.desugarJdkLibs)
        }
    }
}

// endregion
