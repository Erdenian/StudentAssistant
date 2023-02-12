plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    jacoco
}

android {
    namespace = "com.erdenian.studentassistant.database"

    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
            }
        }
    }
}

dependencies {
    // region Private
    api(project(":data:entity"))
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    // endregion

    // region Kotlin
    api(libs.kotlinx.coroutines)
    // endregion

    // region AndroidX
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room)
    // endregion

    // region Core
    kapt(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
