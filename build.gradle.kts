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

        if (extensions.findByType<BaseExtension>()?.buildFeatures?.compose == true) {
            dependencies {
                val implementation by configurations
                implementation(platform(libsAndroidx.compose.bom))
            }
        }
    }
}

typealias JacocoReport = org.gradle.testing.jacoco.tasks.JacocoReport
typealias JacocoReportTask = com.android.build.gradle.internal.coverage.JacocoReportTask
typealias BaseVariant = com.android.build.gradle.api.BaseVariant
typealias AndroidUnitTest = com.android.build.gradle.tasks.factory.AndroidUnitTest

val jacocoMergedReportTask = project.tasks.create("jacocoMergedReport", JacocoReport::class) {
    group = "Reporting"
    description = "Generates Jacoco coverage reports for all variants"

    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)

        val destination = "${project.buildDir}/reports/jacoco"
        csv.outputLocation.set(File("$destination/jacoco.csv"))
        html.outputLocation.set(File("$destination/jacocoHtml"))
        xml.outputLocation.set(File("$destination/jacoco.xml"))
    }
}
subprojects {
    afterEvaluate {
        fun BaseExtension.ifApplication(action: AppExtension.() -> Unit) {
            if (this is AppExtension) action()
        }

        fun BaseExtension.ifLibrary(action: LibraryExtension.() -> Unit) {
            if (this is LibraryExtension) action()
        }

        operator fun FileCollection?.plus(other: FileCollection?): FileCollection = when {
            (this == null) -> checkNotNull(other)
            (other == null) -> this
            else -> this + other
        }

        fun createJacocoTasks(variant: BaseVariant, unitTestCoverage: Boolean, connectedTestCoverage: Boolean): JacocoReport {
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
                val unitTestTask = project.tasks.getByName("test${variant.name.capitalize()}UnitTest", AndroidUnitTest::class)

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

                    reports {
                        html.required.set(true)
                        xml.required.set(false)
                        csv.required.set(false)

                        val destination = "${project.buildDir}/reports/jacoco/${variant.name}/${unitTestTask.name}"
                        html.outputLocation.set(File("$destination/jacocoHtml"))
                        xml.outputLocation.set(File("$destination/jacoco.xml"))
                        csv.outputLocation.set(File("$destination/jacoco.csv"))
                    }
                }
            } else null

            val connectedTest = if (connectedTestCoverage) {
                val connectedTestTask = project.tasks.getByName(
                    "create${variant.name.capitalize()}AndroidTestCoverageReport",
                    JacocoReportTask::class
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

                    reports {
                        html.required.set(true)
                        xml.required.set(false)
                        csv.required.set(false)

                        val destination = "${project.buildDir}/reports/jacoco/${variant.name}/$taskBaseName"
                        html.outputLocation.set(File("$destination/jacocoHtml"))
                        xml.outputLocation.set(File("$destination/jacoco.xml"))
                        csv.outputLocation.set(File("$destination/jacoco.csv"))
                    }
                }
            } else null

            return project.tasks.create(
                "jacoco${variant.name.capitalize()}Report",
                JacocoReport::class,
                configuration
            ).apply {
                group = "Reporting"
                description = "Generates Jacoco coverage reports for the ${variant.name} variant"

                enabled = (unitTest != null) || (connectedTest != null)
                unitTest?.let { dependsOn(it) }
                connectedTest?.let { dependsOn(it) }

                if (enabled) executionData.setFrom(unitTest?.executionData + connectedTest?.executionData)

                reports {
                    html.required.set(true)
                    xml.required.set(false)
                    csv.required.set(false)

                    val destination = "${project.buildDir}/reports/jacoco/${variant.name}"
                    csv.outputLocation.set(File("$destination/jacoco.csv"))
                    html.outputLocation.set(File("$destination/jacocoHtml"))
                    xml.outputLocation.set(File("$destination/jacoco.xml"))
                }
            }
        }

        val hasJacoco = project.plugins.hasPlugin("jacoco")
        if (extensions.findByType<BaseExtension>() != null) extensions.configure<BaseExtension> {
            ifApplication {
                buildTypes {
                    debug {
                        enableUnitTestCoverage = hasJacoco
                        enableAndroidTestCoverage = hasJacoco
                    }
                }
                applicationVariants.all {
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
            ifLibrary {
                buildTypes {
                    debug {
                        enableUnitTestCoverage = hasJacoco
                        enableAndroidTestCoverage = hasJacoco
                    }
                }
                libraryVariants.all {
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
        }
    }
}
