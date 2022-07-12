@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libsPlugins.plugins.android.library.get().pluginId)
    id(libsPlugins.plugins.kotlin.android.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.uikit"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":core:strings"))
    implementation(project(":core:style"))
    // endregion

    // region UI
    implementation(libsUi.accompanist.placeholder)
    // endregion
}
