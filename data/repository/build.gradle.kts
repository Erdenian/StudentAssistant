plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    jacoco
}

android {
    namespace = "com.erdenian.studentassistant.repository"
}

dependencies {
    // region Private
    api(project(":data:entity"))
    implementation(project(":data:database"))
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    // endregion

    // region Kotlin
    api(libs.kotlinx.coroutines)
    // endregion

    // region Core
    kapt(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
