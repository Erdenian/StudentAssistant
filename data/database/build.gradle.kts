plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.erdenian.studentassistant.database"

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
    testImplementation(libsTest.junit)
    androidTestImplementation(libsTest.bundles.android)
    // endregion

    // region Kotlin
    api(libsKotlinx.coroutines)
    // endregion

    // region AndroidX
    kapt(libsAndroidx.room.compiler)
    implementation(libsAndroidx.room)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion
}
