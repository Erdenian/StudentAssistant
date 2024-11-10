plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.erdenian.studentassistant.navigation"
}

dependencies {
    // region Private
    implementation(project(":core:strings"))
    implementation(project(":core:style"))
    // endregion
}
