plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false

    alias(libs.plugins.detekt)
    jacoco

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
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi"
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
                targetSdk = config.versions.targetSdk.get().toInt()

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
            apiLevel = 31
            systemImageSource = "aosp"
        }

        dependencies {
            if (buildFeatures.compose == true) "implementation"(platform(libs.androidx.compose.bom))

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
        project.tasks.all {
            fun Task.removeIf(condition: Boolean) {
                if (condition) {
                    enabled = false
                    dependsOn.clear()
                    logger.info("Disabled ${project.path}:$name task")
                }
            }

            when {
                (this is com.android.build.gradle.tasks.factory.AndroidUnitTest) -> removeIf(!unitTestExists)
                (this is com.android.build.gradle.internal.tasks.ManagedDeviceSetupTask) ||
                        (this is com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask) ||
                        (this is com.android.build.gradle.internal.coverage.JacocoReportTask) ||
                        name.contains("AndroidTest") -> removeIf(!androidTestExists)
            }
        }
    }
}

// endregion

// region Jacoco

jacoco.toolVersion = libs.versions.plugins.jacoco.get()
val jacocoIgnoredFlavors = listOf<String>()

fun JacocoReport.setupReports(basePath: String) {
    reports {
        html.required.set(true)
        xml.required.set(true)
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
    doLast { project.logger.lifecycle("View coverage report at ${reports.html.outputLocation.file("index.html").get()}") }
}

subprojectsAfterEvaluate {
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

        val unitTestTask = project.tasks.findByName(
            "test${variant.name.capitalize()}UnitTest"
        ) as com.android.build.gradle.tasks.factory.AndroidUnitTest?
        val unitTestReportTask = if (unitTestCoverage && (unitTestTask?.enabled == true)) {
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

        val connectedTestTask = project.tasks.findByName(
            "createManagedDevice${variant.name.capitalize()}AndroidTestCoverageReport",
        ) as? com.android.build.gradle.internal.coverage.JacocoReportTask
        val connectedTestReportTask = if (connectedTestCoverage && (connectedTestTask?.enabled == true)) {
            connectedTestTask.doFirst {
                connectedTestTask.jacocoConnectedTestsCoverageDir.file("coverage.ec").get().asFile.createNewFile()
            }

            val taskBaseName = "connected${variant.name.capitalize()}AndroidTest"
            project.tasks.create(
                "jacoco${taskBaseName.capitalize()}Report",
                JacocoReport::class,
                configuration
            ).apply {
                description = "Generates Jacoco coverage reports for the ${variant.name} variant connected tests"
                dependsOn(connectedTestTask.taskDependencies)

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

            enabled = (unitTestReportTask != null) || (connectedTestReportTask != null)
            unitTestReportTask?.let { dependsOn(it) }
            connectedTestReportTask?.let { dependsOn(it) }

            if (enabled && (unitTestReportTask != null) || (connectedTestReportTask != null)) {
                operator fun FileCollection?.plus(other: FileCollection?): FileCollection = when {
                    (this == null) -> checkNotNull(other)
                    (other == null) -> this
                    else -> this + other
                }
                executionData.setFrom(unitTestReportTask?.executionData + connectedTestReportTask?.executionData)
            }
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
                if (productFlavors.any { jacocoIgnoredFlavors.contains(it.name) }) return@all
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

// endregion

// region Release

tasks.register("updateChangelog") {
    doFirst {
        val file = project.file("CHANGELOG.md")
        val lines = file.readLines().toMutableList()
        val lineSeparator = System.lineSeparator()

        val android = subprojects
            .first { it.name == "app" }
            .extensions
            .getByType<com.android.build.gradle.BaseExtension>()

        val newVersion = checkNotNull(android.defaultConfig.versionName)
        val oldVersion = lines
            .first { it.startsWith("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/") }
            .removePrefix("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/")
            .removeSuffix("...develop")

        lines.add(
            lines.indexOf("## [Unreleased]") + 1,
            "$lineSeparator## [$newVersion] - ${java.time.LocalDate.now()}"
        )

        lines.set(
            lines.indexOf("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$oldVersion...develop"),
            "[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$newVersion...develop"
        )

        lines.add(
            lines.indexOf("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$newVersion...develop") + 1,
            "[$newVersion]: https://github.com/Erdenian/StudentAssistant/compare/$oldVersion...$newVersion"
        )

        file.delete()
        file.writeText(lines.joinToString("$lineSeparator") + "$lineSeparator")
    }
}

// endregion
