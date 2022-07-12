@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libsPlugins.plugins.android.library.get().pluginId)
    id(libsPlugins.plugins.kotlin.android.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.style"

    buildFeatures.compose = true
}

dependencies {
    // region Compose
    api(libsAndroidx.bundles.compose)
    //endregion
}
