@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.schedule"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    implementation(project(":common:sampledata"))

    implementation(project(":data:repository"))
    // endregion

    // region AndroidX
    implementation(libs.androidx.lifecycle.viewmodel)
    // endregion

    // region Core
    kapt(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion

    // region UI
    implementation(libs.ui.accompanist.placeholder)
    // endregion
}
