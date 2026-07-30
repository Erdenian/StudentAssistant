@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
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

val reportMerge = tasks.register<dev.detekt.gradle.report.ReportMergeTask>("reportMerge") {
    group = "verification"
    description = "Merges all Detekt SARIF reports into a single file"

    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))

    doLast {
        val sarifFile = output.get().asFile
        if (sarifFile.exists()) {
            val content = sarifFile.readText()

            // Парсим JSON через регулярное выражение, чтобы избежать проблем с classpath зависимостями
            val ruleRegex = """"ruleId"\s*:\s*"([^"]+)"""".toRegex()
            val counts = ruleRegex.findAll(content)
                .map { it.groupValues[1] }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }

            if (counts.isNotEmpty()) {
                val total = counts.sumOf { it.second }
                println("\n" + "=".repeat(65))
                println("📊 DETEKT ISSUES SUMMARY (Total: $total)")
                println("=".repeat(65))
                counts.forEach { (rule, count) ->
                    println("${rule.padEnd(60, ' ')} : $count")
                }
                println("=".repeat(65) + "\n")
            } else {
                println("\n🎉 No Detekt issues found!\n")
            }
        }
    }
}

subprojects {
    pluginManager.apply(rootProject.libs.plugins.detekt.get().pluginId)

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
        detektPlugins(rootProject.libs.detekt.rulesCompose)
    }

    detekt {
        toolVersion = rootProject.libs.versions.plugins.detekt.get()
        buildUponDefaultConfig = true
        baseline = file("detekt/baseline.xml")
        parallel = false // https://github.com/detekt/detekt/issues/9121
    }

    tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
        reports {
            checkstyle.required.set(false)
            html.required.set(true)
            sarif.required.set(true)
            markdown.required.set(false)
        }
        finalizedBy(reportMerge)
    }

    reportMerge {
        input.from(tasks.withType<dev.detekt.gradle.Detekt>().map { it.reports.sarif.outputLocation })
    }
}

tasks.register<Delete>("clean") {
    group = "build"
    description = "Deletes the build directory"

    delete(rootProject.layout.buildDirectory)
}

// region Extensions

fun subprojectsAfterEvaluate(action: Action<in Project>) = subprojects { afterEvaluate(action) }

typealias AndroidExtensions = com.android.build.api.dsl.CommonExtension

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
        project.pluginManager.apply(libs.plugins.android.lint.get().pluginId)
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

        compileOptions.apply {
            isCoreLibraryDesugaringEnabled = true

            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        packaging.apply {
            resources {
                excludes += "META-INF/LICENSE.md"
                excludes += "META-INF/LICENSE-notice.md"
            }
        }

        testOptions.unitTests.all { it.jvmArgs("--add-opens=java.base/java.time=ALL-UNNAMED") }
        //noinspection WrongGradleMethod
        tasks.withType(Test::class) { jvmArgs = listOf("-XX:+EnableDynamicAgentLoading") }

        val androidTestDir = project.file("src/androidTest")
        //noinspection WrongGradleMethod
        val androidTestExists = androidTestDir.exists() && androidTestDir.walk().any { it.isFile }

        if (androidTestExists) {
            testOptions.managedDevices.localDevices.create("testDevice") {
                device = "Pixel 4"
                apiLevel = 34
                systemImageSource = "aosp-atd"
                testedAbi = "x86_64"
            }
        }

        //noinspection WrongGradleMethod
        project.dependencies {
            if (project.plugins.hasPlugin(libs.plugins.kotlin.compose.get().pluginId)) {
                val bom = platform(libs.androidx.compose.bom)

                if (project.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)) "api"(bom)
                else "implementation"(bom)

                "androidTestImplementation"(bom)
            }

            configurations.findByName("coreLibraryDesugaring")?.invoke(libs.androidTools.desugarJdkLibs)
        }
    }
}

// endregion
