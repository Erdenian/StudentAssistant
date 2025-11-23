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
    api(libs.androidx.navigation)
    api(libs.androidx.compose.animation)
    // endregion
}
