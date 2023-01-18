@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libsPlugins.plugins.android.library.get().pluginId)
    id(libsPlugins.plugins.kotlin.android.get().pluginId)
    jacoco
}

android {
    namespace = "com.erdenian.studentassistant.utils"
}

dependencies {
    // region Tests
    testImplementation(libsTest.junit)
    // endregion
}
