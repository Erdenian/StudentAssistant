plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kover.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.utils"
}

dependencies {
    // region Tests
    testImplementation(libs.bundles.test.unit)
    // endregion
}
