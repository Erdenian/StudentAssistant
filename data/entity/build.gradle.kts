plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    jacoco
}

android {
    namespace = "com.erdenian.studentassistant.entity"
}

dependencies {
    // region Tests
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    // endregion
}
