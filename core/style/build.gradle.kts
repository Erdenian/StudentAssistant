plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.erdenian.studentassistant.style"
}

dependencies {
    // region Compose
    api(libs.bundles.androidx.compose)
    // endregion

    // region AndroidX
    implementation(libs.androidx.appcompat)
    // endregion
}
