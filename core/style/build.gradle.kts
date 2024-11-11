plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.erdenian.studentassistant.style"
}

dependencies {
    // region Compose
    api(libs.bundles.androidx.compose)
    // endregion
}
