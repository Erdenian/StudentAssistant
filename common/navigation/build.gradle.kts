plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.erdenian.studentassistant.navigation"
}

dependencies {
    api(libs.androidx.navigation)
}
