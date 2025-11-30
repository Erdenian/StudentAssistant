plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kover)
}

android {
    namespace = "ru.erdenian.studentassistant.utils"
}

dependencies {
    // region Tests
    testImplementation(libs.bundles.test.unit)
    // endregion

    // region Kotlin
    implementation(libs.kotlinx.coroutines)
    // endregion
}
