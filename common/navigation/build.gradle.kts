plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.erdenian.studentassistant.navigation"
}

dependencies {
    // region AndroidX
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation3.runtime)
    api(libs.androidx.compose.animation)
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    // endregion
}
