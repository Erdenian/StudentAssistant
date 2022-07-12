@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libsPlugins.plugins.android.library.get().pluginId)
    id(libsPlugins.plugins.kotlin.android.get().pluginId)
    id(libsPlugins.plugins.kotlin.kapt.get().pluginId)
}

android {
    buildFeatures.compose = true

    namespace = "com.erdenian.studentassistant.homeworks"
}

dependencies {
    // region Private
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    implementation(project(":common:sampledata"))

    implementation(project(":data:repository"))
    // endregion

    // region AndroidX
    implementation(libsAndroidx.lifecycle.viewmodel)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion

    // region UI
    implementation(libsUi.accompanist.placeholder)
    // endregion
}
