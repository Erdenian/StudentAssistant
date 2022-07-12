@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libsPlugins.plugins.android.library.get().pluginId)
    id(libsPlugins.plugins.kotlin.android.get().pluginId)
    id(libsPlugins.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.settings"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":data:repository"))
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    // endregion

    // region AndroidX
    implementation(libsAndroidx.lifecycle.viewmodel)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion
}
