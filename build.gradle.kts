@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libsPlugins.plugins.android.application) apply false
    alias(libsPlugins.plugins.android.library) apply false
    alias(libsPlugins.plugins.kotlin.android) apply false

    alias(libsPlugins.plugins.detekt)
}

detekt {
    config = files("detekt-config.yml")
    source = files(*subprojects.map { "${it.projectDir}/src" }.toTypedArray())
}

dependencies {
    detektPlugins(libsPlugins.detekt.formatting)
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",

                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi"
            )
        }
    }
}

typealias BaseExtension = com.android.build.gradle.BaseExtension
typealias AppExtension = com.android.build.gradle.internal.dsl.BaseAppModuleExtension
typealias LibraryExtension = com.android.build.gradle.LibraryExtension
typealias ManagedVirtualDevice = com.android.build.api.dsl.ManagedVirtualDevice
subprojects {
    afterEvaluate {
        fun BaseExtension.ifApplication(action: AppExtension.() -> Unit) {
            if (this is AppExtension) action()
        }

        fun BaseExtension.ifLibrary(action: LibraryExtension.() -> Unit) {
            if (this is LibraryExtension) action()
        }

        if (extensions.findByType<BaseExtension>() != null) extensions.configure<BaseExtension> {
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
                    targetSdk = config.versions.targetSdk.get().toInt()

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            composeOptions {
                kotlinCompilerExtensionVersion = libsAndroidx.versions.compose.compiler.get()
            }

            ifLibrary {
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                    }
                }
            }

            compileOptions {
                isCoreLibraryDesugaringEnabled = true

                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }

            run { // Setup device for testing if necessary
                val androidTest = sourceSets.findByName("androidTest")
                    ?.java
                    ?.srcDirs
                    ?.single()
                    ?.absoluteFile
                    ?.parentFile

                if (androidTest?.walkTopDown()?.any { it.isFile } == true) {
                    testOptions {
                        managedDevices {
                            devices {
                                create<ManagedVirtualDevice>("testDevice") {
                                    device = "Pixel 4"
                                    apiLevel = 31
                                    systemImageSource = "aosp"
                                }
                            }
                        }
                    }
                }
            }
        }

        dependencies {
            configurations.findByName("coreLibraryDesugaring")?.invoke(libsAndroidTools.desugarJdkLibs)
        }
    }
}
