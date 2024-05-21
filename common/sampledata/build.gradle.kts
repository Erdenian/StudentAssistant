plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.compose.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.sampledata"
}

dependencies {
    // region Private
    implementation(project(":core:style"))
    implementation(project(":data:entity"))
    // endregion
}
