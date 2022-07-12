plugins {
    val androidPluginVersion = "7.2.1"
    id("com.android.application") version androidPluginVersion apply false
    id("com.android.library") version androidPluginVersion apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false

    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("ru.erdenian.shrinkometer") version "0.3.1" apply false
}

detekt {
    config = files("detekt-config.yml")
    source = files(*subprojects.map { "${it.projectDir}/src" }.toTypedArray())
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")
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
        }

        dependencies {
            configurations.findByName("coreLibraryDesugaring")?.invoke(libsAndroidTools.desugarJdkLibs)
        }
    }
}
