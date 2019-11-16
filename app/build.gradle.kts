import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
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
    val kotlinVersion: String by project
    val lifecycleVersion: String by project
    val navigationVersion: String by project
    val kodeinVersion: String by project

    // region Private
    implementation(project(":repository"))
    implementation(project(":uikit"))
    implementation(project(":utils"))
    // endregion

    // region Kotlin
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    // endregion

    // region AndroidX
    implementation("androidx.fragment:fragment-ktx:1.2.0-rc02")
    implementation("androidx.drawerlayout:drawerlayout:1.1.0-alpha03")
    implementation("androidx.viewpager:viewpager:1.0.0")

    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
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
