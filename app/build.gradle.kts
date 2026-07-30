@file:Suppress("UnstableApiUsage")

import java.time.LocalDate
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)

    alias(libs.plugins.tripletPlay)
}

// Используем Provider API для чтения файла.
// Это позволяет Gradle отслеживать изменения в файле и инвалидировать кэш конфигурации автоматически.
val supportedLocalesProvider = providers
    .fileContents(layout.projectDirectory.file("src/main/res/xml/locale_config.xml"))
    .asText
    .map { content ->
        Regex("android:name=\"([a-z]{2,3})\"")
            .findAll(content)
            .map { it.groupValues[1] }
            .toSet()
    }

android {
    namespace = "ru.erdenian.studentassistant"

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = 29
        versionName = "0.8.0"

        androidResources.localeFilters += supportedLocalesProvider.getOrElse(emptySet())

        base.archivesName = "${rootProject.name}-$versionName"

        testInstrumentationRunner = "ru.erdenian.studentassistant.TestRunner"
    }

    // Workaround для "Unable to strip the following libraries, packaging them as they are: libandroidx.graphics.path.so."
    // https://issuetracker.google.com/issues/237187538
    // https://issuetracker.google.com/issues/271316809
    // Та же версия NDK должна быть установлена в шаге android-actions/setup-android в рабочих процессах GitHub Actions.
    ndkVersion = "29.0.14206865"

    lint {
        checkDependencies = true
        checkAllWarnings = true
        checkTestSources = true
    }

    signingConfigs {
        val localProperties = File("${rootDir.path}/local.properties").run {
            if (exists()) `java.util`.Properties().apply { load(inputStream()) } else null
        }
        val environment = System.getenv()
        fun get(env: String, local: String) = environment[env] ?: run {
            project.logger.info("No $env environmental variable")
            localProperties?.getProperty(local) ?: run {
                project.logger.info("No $local local property")
                null
            }
        }

        data class Keystore(
            val storeFile: File,
            val storePassword: String,
            val keyAlias: String,
            val keyPassword: String,
        )

        fun getReleaseKeystore(): Keystore? {
            return Keystore(
                rootProject.file("signing/release.jks"),
                get("ANDROID_KEYSTORE_PASSWORD", "signing.keystorePassword") ?: return null,
                get("ANDROID_KEY_ALIAS", "signing.keyAlias") ?: return null,
                get("ANDROID_KEY_PASSWORD", "signing.keyPassword") ?: return null,
            )
        }

        getByName("debug") {
            storeFile = rootProject.file("signing/debug.jks")
            storePassword = "debugdebug"
            keyAlias = "debug"
            keyPassword = "debugdebug"

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }

        getReleaseKeystore()?.let { keystore ->
            create("release") {
                storeFile = keystore.storeFile
                storePassword = keystore.storePassword
                keyAlias = keystore.keyAlias
                keyPassword = keystore.keyPassword

                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        } ?: project.logger.warn("w: Can't create release signing config")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }
}

dependencies {
    // region Private
    implementation(project(":core:style"))
    implementation(project(":core:strings"))

    implementation(project(":common:utils"))
    implementation(project(":common:navigation"))

    implementation(project(":features:repository"))
    implementation(project(":features:repository:api"))
    implementation(project(":features:schedule"))
    implementation(project(":features:schedule:api"))
    implementation(project(":features:homework"))
    implementation(project(":features:homework:api"))
    implementation(project(":features:settings"))
    implementation(project(":features:settings:api"))
    implementation(project(":features:analytics"))
    implementation(project(":features:analytics:api"))
    // endregion

    // region Kotlin
    implementation(libs.kotlinx.serialization)
    // endregion

    // region AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.core.splashscreen)
    // endregion

    // region Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion

    // region Tests
    androidTestImplementation(libs.bundles.test.android)
    androidTestImplementation(libs.bundles.test.compose)
    debugImplementation(libs.test.compose.manifest)
    // endregion
}

dependencies {
    rootProject.subprojects {
        afterEvaluate {
            pluginManager.apply(libs.plugins.kover.get().pluginId)
            kover(project(path))
        }
    }
}

play {
    track.set("beta")
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.DRAFT)
    defaultToAppBundles.set(true)
}

// region Release

rootProject.tasks.register("updateChangelog") {
    group = "release"
    description = "Updates CHANGELOG.md with the new version and clears play store release notes"

    val changelogFile = rootProject.file("CHANGELOG.md")
    val newVersion = checkNotNull(android.defaultConfig.versionName)
    val releaseNotesDir = file("src/main/play/release-notes")

    doFirst {
        val lines = changelogFile.readLines().toMutableList()
        val lineSeparator = System.lineSeparator()

        val oldVersion = lines
            .first { it.startsWith("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/") }
            .removePrefix("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/")
            .removeSuffix("...develop")

        lines.add(
            lines.indexOf("## [Unreleased]") + 1,
            "$lineSeparator## [$newVersion] - ${LocalDate.now()}",
        )

        lines.set(
            lines.indexOf("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$oldVersion...develop"),
            "[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$newVersion...develop",
        )

        lines.add(
            lines.indexOf("[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/$newVersion...develop") + 1,
            "[$newVersion]: https://github.com/Erdenian/StudentAssistant/compare/$oldVersion...$newVersion",
        )

        changelogFile.delete()
        changelogFile.writeText(lines.joinToString(lineSeparator) + lineSeparator)

        // Сбрасываем содержимое файлов release notes, чтобы они появились в git status
        if (releaseNotesDir.exists()) {
            releaseNotesDir.walk().filter { it.isFile && it.name == "beta.txt" }.forEach { file ->
                file.writeText("TODO: Обновите release notes для ${file.parentFile.name}\n")
            }
        }
    }
}

// endregion

// region Screenshots

abstract class GenerateScreenshotsTask : DefaultTask() {

    @get:Input
    abstract val adbPath: Property<String>

    @get:Input
    abstract val appPackage: Property<String>

    @get:Input
    abstract val testPackage: Property<String>

    @get:Input
    abstract val testRunner: Property<String>

    @get:Input
    abstract val testClass: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileTree

    @get:Internal
    abstract val tempDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun run() {
        val adb = adbPath.get()
        val pkg = appPackage.get()
        val deviceDir = "/sdcard/Android/data/$pkg/files/screenshots"
        val localTemp = tempDir.get().asFile
        val localFinal = outputDir.get().asFile

        // Вспомогательная функция для запуска adb
        fun adb(vararg args: String) {
            val stdout = `java.io`.ByteArrayOutputStream()
            execOperations.exec {
                executable = adb
                args(*args)
                standardOutput = stdout
            }
            val output = stdout.toString()
            println(output)

            // Дополнительно проверяем есть ли проваленные тесты
            if (output.contains("FAILURES!!!")) {
                throw GradleException("Instrumentation tests failed. See output above.")
            }
        }

        fun shell(vararg command: String) {
            adb("shell", *command)
        }

        println("=== 1. Очистка старых скриншотов на устройстве ===")
        shell("rm", "-rf", deviceDir)

        // @formatter:off
        println("=== 2. Настройка Demo Mode и отключение анимаций ===")
        // Demo Mode
        shell("settings", "put", "global", "sysui_demo_allowed", "1")
        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "enter")
        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "clock", "-e", "hhmm", "1400")
        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "network", "-e", "mobile", "show", "-e", "level", "4", "-e", "datatype", "lte")
        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "network", "-e", "wifi", "show", "-e", "level", "4", "-e", "fully", "true")
        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "battery", "-e", "level", "100", "-e", "plugged", "false")
        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "notifications", "-e", "visible", "false")
        
        // Отключение анимаций (0 = выкл)
        shell("settings", "put", "global", "window_animation_scale", "0")
        shell("settings", "put", "global", "transition_animation_scale", "0")
        shell("settings", "put", "global", "animator_duration_scale", "0")

        println("=== 3. Запуск теста генерации скриншотов ===")
        // Передаем аргумент is_screenshot_mode=true
        shell("am", "instrument", "-w", "-r", "-e", "class", testClass.get(), "-e", "is_screenshot_mode", "true", "${testPackage.get()}/${testRunner.get()}")
        // @formatter:on

        println("=== 4. Выключение Demo Mode и включение анимаций ===")
        // Включение анимаций (1 = вкл)
        shell("settings", "put", "global", "window_animation_scale", "1")
        shell("settings", "put", "global", "transition_animation_scale", "1")
        shell("settings", "put", "global", "animator_duration_scale", "1")

        shell("am", "broadcast", "-a", "com.android.systemui.demo", "-e", "command", "exit")

        println("=== 5. Копирование скриншотов в проект ===")
        // Очищаем локальную временную папку
        localTemp.deleteRecursively()
        localTemp.mkdirs()

        adb("pull", "$deviceDir/.", localTemp.absolutePath)

        localTemp.listFiles()?.forEach { langDir ->
            if (!langDir.isDirectory) return@forEach

            val langCode = langDir.name
            val targetDir = localFinal.resolve("$langCode/graphics/phone-screenshots")

            println("Processing $langCode -> $targetDir")
            targetDir.mkdirs()

            // Удаляем старые png
            targetDir.listFiles { it.extension == "png" }?.forEach { it.delete() }

            // Копируем новые
            fs.copy {
                from(langDir)
                into(targetDir)
                include("*.png")
            }
        }

        println("=== 6. Очистка временных файлов ===")
        localTemp.deleteRecursively()

        println("=== Готово! Скриншоты обновлены. ===")
    }
}

tasks.register<GenerateScreenshotsTask>("generateScreenshots") {
    group = "android"
    description = "Generates screenshots for all supported locales using an automated test."

    val buildType = "debug"

    dependsOn(tasks.named("install${buildType.capitalized()}"))
    dependsOn(tasks.named("install${buildType.capitalized()}AndroidTest"))

    val android = project.extensions.getByType<com.android.build.api.dsl.ApplicationExtension>()
    val androidComponents =
        project.extensions.getByType<com.android.build.api.variant.ApplicationAndroidComponentsExtension>()
    val applicationId = checkNotNull(android.defaultConfig.applicationId)
    val debugSuffix = checkNotNull(android.buildTypes.getByName(buildType).applicationIdSuffix)
    val pkg = applicationId + debugSuffix

    adbPath.set(androidComponents.sdkComponents.adb.map { it.asFile.absolutePath })
    appPackage.set(pkg)
    testPackage.set("$pkg.test")
    testRunner.set(checkNotNull(android.defaultConfig.testInstrumentationRunner))

    val testClassName = "ru.erdenian.studentassistant.AutomatedScreenshotTest"
    testClass.set(testClassName)

    // Валидация существования файла теста на этапе конфигурации Gradle.
    val relativeTestPath = "src/androidTest/kotlin/" + testClassName.replace('.', '/') + ".kt"
    val testFile = layout.projectDirectory.file(relativeTestPath)
    if (!testFile.asFile.exists()) {
        throw GradleException("Test source file not found for class $testClassName. Expected at: $relativeTestPath")
    }

    // Отслеживаем всю папку src, НО исключаем папку с ресурсами Play Store (куда мы пишем скриншоты),
    // чтобы избежать циклического перезапуска задачи.
    sources.from(layout.projectDirectory.dir("src"))
    sources.exclude("main/play/**")

    tempDir.set(layout.buildDirectory.dir("screenshots_tmp"))
    outputDir.set(layout.projectDirectory.dir("src/main/play/listings"))
}

// endregion
