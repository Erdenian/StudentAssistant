plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.ksp.get().pluginId)
    id(libs.plugins.kotlin.compose.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.schedule"
}

dependencies {
    // region Private
    implementation(project(":features:schedule:api"))

    implementation(project(":core:mediator"))
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    implementation(project(":common:sampledata"))
    implementation(project(":features:homeworks:api"))

    implementation(project(":data:repository"))
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
