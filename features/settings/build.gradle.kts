plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.ksp.get().pluginId)
    id(libs.plugins.kotlin.compose.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.settings"
}

dependencies {
    // region Private
    implementation(project(":features:settings:api"))

    implementation(project(":core:mediator"))
    implementation(project(":data:repository"))
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    // endregion

    // region AndroidX
    implementation(libs.androidx.lifecycle.viewmodel)
    // endregion

    // region Navigation
    implementation(libs.navigation.voyager.navigator)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
