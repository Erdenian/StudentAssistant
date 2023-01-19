@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libsPlugins.plugins.android.application) apply false
    alias(libsPlugins.plugins.android.library) apply false
    alias(libsPlugins.plugins.kotlin.android) apply false

    alias(libsPlugins.plugins.detekt)
    jacoco
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
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi"
            )
        }
    }
}

// endregion

// region Android

run {
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

            run { // Setup device for testing if necessary
                val androidTest = sourceSets.findByName("androidTest")
                    ?.java
                    ?.srcDirs
                    ?.single()
                    ?.absoluteFile
                    ?.parentFile

                if (androidTest?.walk()?.any { it.isFile } == true) {
                    testOptions {
                        managedDevices {
                            devices {
                                create<com.android.build.api.dsl.ManagedVirtualDevice>("testDevice") {
                                    device = "Pixel 4"
                                    apiLevel = 31
                                    systemImageSource = "aosp"
                                }
                            }
                        }
                    }
                }
            }

            dependencies {
                if (buildFeatures.compose == true) "implementation"(platform(libsAndroidx.compose.bom))

                configurations.findByName("coreLibraryDesugaring")?.invoke(libsAndroidTools.desugarJdkLibs)
            }
        }
    }
}

// endregion

// region Jacoco

run {
    fun JacocoReport.setupReports(basePath: String) {
        reports {
            html.required.set(true)
            xml.required.set(false)
            csv.required.set(false)

            html.outputLocation.set(File("$basePath/jacocoHtml"))
            xml.outputLocation.set(File("$basePath/jacoco.xml"))
            csv.outputLocation.set(File("$basePath/jacoco.csv"))
        }
    }

    val jacocoMergedReportTask = project.tasks.create("jacocoMergedReport", JacocoReport::class) {
        group = "Reporting"
        description = "Generates Jacoco coverage reports for all variants"

        setupReports("${project.buildDir}/reports/jacoco")
    }

    subprojectsAfterEvaluate {
        operator fun FileCollection?.plus(other: FileCollection?): FileCollection = when {
            (this == null) -> checkNotNull(other)
            (other == null) -> this
            else -> this + other
        }

        fun createJacocoTasks(
            variant: com.android.build.gradle.api.BaseVariant,
            unitTestCoverage: Boolean,
            connectedTestCoverage: Boolean
        ): JacocoReport {
            val configuration = Action<JacocoReport> {
                group = "Reporting"

                val sourceDirs = variant.sourceSets.asSequence()
                    .map { it.kotlinDirectories }
                    .flatten()
                    .map { it.path }
                    .toList()
                    .let { project.files(it) }
                val generatedDirs = project.files(
                    "${project.buildDir}/generated/source/buildConfig/${variant.name}",
                    "${project.buildDir}/generated/source/kapt/${variant.name}",
                    "${project.buildDir}/generated/source/kaptKotlin/${variant.name}"
                )

                val javaClassesTree = variant.javaCompileProvider.get().destinationDirectory.asFileTree
                val kotlinClassesTree = project.fileTree("${project.buildDir}/tmp/kotlin-classes/${variant.name}")

                sourceDirectories.setFrom(sourceDirs + generatedDirs)
                classDirectories.setFrom(javaClassesTree + kotlinClassesTree)
            }

            val unitTest = if (unitTestCoverage) {
                val unitTestTask = project.tasks.getByName(
                    "test${variant.name.capitalize()}UnitTest",
                    com.android.build.gradle.tasks.factory.AndroidUnitTest::class
                )

                project.tasks.create(
                    "jacoco${unitTestTask.name.capitalize()}Report",
                    JacocoReport::class,
                    configuration
                ).apply {
                    description = "Generates Jacoco coverage reports for the ${variant.name} variant unit tests"
                    dependsOn(unitTestTask)

                    val executionFilePath =
                        "${project.buildDir}/outputs/unit_test_code_coverage/${variant.name}UnitTest/test${variant.name.capitalize()}UnitTest.exec"
                    executionData.setFrom(project.file(executionFilePath))
                    setupReports("${project.buildDir}/reports/jacoco/${variant.name}/${unitTestTask.name}")
                }
            } else null

            val connectedTest = if (connectedTestCoverage) {
                val connectedTestTask = project.tasks.getByName(
                    "create${variant.name.capitalize()}AndroidTestCoverageReport",
                    com.android.build.gradle.internal.coverage.JacocoReportTask::class
                )

                val taskBaseName = "connected${variant.name.capitalize()}AndroidTest"
                project.tasks.create(
                    "jacoco${taskBaseName.capitalize()}Report",
                    JacocoReport::class,
                    configuration
                ).apply {
                    description = "Generates Jacoco coverage reports for the ${variant.name} variant connected tests"
                    dependsOn(connectedTestTask)

                    executionData.setFrom(connectedTestTask.jacocoConnectedTestsCoverageDir.asFileTree)
                    setupReports("${project.buildDir}/reports/jacoco/${variant.name}/$taskBaseName")
                }
            } else null

            return project.tasks.create(
                "jacoco${variant.name.capitalize()}Report",
                JacocoReport::class,
                configuration
            ).apply {
                description = "Generates Jacoco coverage reports for the ${variant.name} variant"

                enabled = (unitTest != null) || (connectedTest != null)
                unitTest?.let { dependsOn(it) }
                connectedTest?.let { dependsOn(it) }

                if (enabled) executionData.setFrom(unitTest?.executionData + connectedTest?.executionData)
                setupReports("${project.buildDir}/reports/jacoco/${variant.name}")
            }
        }

        val hasJacoco = project.plugins.hasPlugin("jacoco")
        configureAndroidIfExists {
            val buildTypeAction: com.android.build.api.dsl.BuildType.() -> Unit = {
                enableUnitTestCoverage = hasJacoco
                enableAndroidTestCoverage = hasJacoco
            }

            fun configure(variants: DomainObjectCollection<out com.android.build.gradle.api.BaseVariant>) {
                variants.all {
                    if (!buildType.isDebuggable) return@all
                    val reportTask = createJacocoTasks(this, unitTestCoverage = hasJacoco, connectedTestCoverage = hasJacoco)
                    if (reportTask.isEnabled) jacocoMergedReportTask.dependsOn(reportTask)
                    jacocoMergedReportTask.apply {
                        sourceDirectories.setFrom(sourceDirectories + reportTask.sourceDirectories)
                        classDirectories.setFrom(classDirectories + reportTask.classDirectories)
                        executionData.setFrom(executionData + reportTask.executionData)
                    }
                }
            }

            ifApplication {
                buildTypes { debug(buildTypeAction) }
                configure(applicationVariants)
            }
            ifLibrary {
                buildTypes { debug(buildTypeAction) }
                configure(libraryVariants)
            }
        }
    }
}

// endregion
