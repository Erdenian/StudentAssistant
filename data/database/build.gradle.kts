plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
}

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
    }
}

dependencies {
    // region Private
    api(project(":data:entity"))
    // endregion

    // region Tests
    val junitVersion: String by project
    testImplementation("junit:junit:$junitVersion")

    val junitKtxVersion: String by project
    androidTestImplementation("androidx.test.ext:junit-ktx:$junitKtxVersion")

    val androidxTestVersion: String by project
    androidTestImplementation("androidx.test:core-ktx:$androidxTestVersion")
    androidTestImplementation("androidx.test:runner:$androidxTestVersion")
    // endregion

    // region Kotlin
    val coroutinesVersion: String by project
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    // endregion

    // region AndroidX
    val roomVersion: String by project
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // endregion

    // region Core
    val daggerVersion: String by project
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    val kodeinVersion: String by project
    implementation("org.kodein.di:kodein-di-jvm:$kodeinVersion")
    // endregion
}
