plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.erdenian.studentassistant.sampledata"
}

dependencies {
    // region Private
    implementation(project(":core:style"))
    implementation(project(":features:repository:api"))
    // endregion
}
