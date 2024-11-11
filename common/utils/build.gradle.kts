plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.erdenian.studentassistant.utils"
}

dependencies {
    // region Tests
    testImplementation(libs.bundles.test.unit)
    // endregion
}
