plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.style"

    buildFeatures.compose = true
}

dependencies {
    // region Compose
    api(libs.bundles.androidx.compose)
    // endregion
}
