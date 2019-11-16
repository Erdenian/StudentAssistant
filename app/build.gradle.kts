import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("de.mannodermaus.android-junit5")
}

android {
    val compileSdkVersion: String by project
    val targetSdkVersion: String by project

    compileSdkVersion(compileSdkVersion.toInt())

    defaultConfig {
        applicationId = "ru.erdenian.studentassistant"
        versionCode = 12
        versionName = "0.3.1"

        minSdkVersion(21)
        targetSdkVersion(targetSdkVersion.toInt())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument(
            "runnerBuilder",
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
        )

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        // JUnit 5
        exclude("META-INF/LICENSE*")
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")

        productFlavors.forEach { flavor ->
            getByName(flavor.name).java.srcDirs("src/${flavor.name}/kotlin")
            "test${flavor.name.capitalize()}".let { getByName(it).java.srcDirs("src/$it/kotlin") }
            "androidTest${flavor.name.capitalize()}".let { getByName(it).java.srcDirs("src/$it/kotlin") }
        }
    }

    applicationVariants.all {
        outputs.forEach { output ->
            output as BaseVariantOutputImpl
            output.apply {
                outputFileName = outputFileName.replace(
                    project.name, "${rootProject.name}-${defaultConfig.versionName}"
                )
            }
        }
    }
}

dependencies {
    val junitVersion = "5.5.2"
    val androidTestVersion = "1.1.0"

    val kotlinVersion: String by project
    val coroutinesVersion: String by project

    val lifecycleVersion: String by project
    val navigationVersion: String by project
    val roomVersion: String by project

    val kodeinVersion: String by project

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:$androidTestVersion")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:$androidTestVersion")

    implementation(project(":entity"))
    implementation(project(":utils"))
    implementation(project(":customviews"))

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    // endregion

    // region AndroidX
    implementation("androidx.fragment:fragment-ktx:1.2.0-rc02")
    implementation("androidx.drawerlayout:drawerlayout:1.1.0-alpha03")
    implementation("androidx.viewpager:viewpager:1.0.0")

    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // endregion

    // region DI
    implementation("org.kodein.di:kodein-di-generic-jvm:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")
    // endregion

    implementation("org.jetbrains.anko:anko-common:0.10.8")

    // region UI
    implementation("com.google.android.material:material:1.2.0-alpha01")
    implementation("com.github.DavidProdinger:weekdays-selector:1.1.0")
    // endregion
}
