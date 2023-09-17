plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.ksp.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    jacoco
}

android {
    namespace = "com.erdenian.studentassistant.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
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
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
