plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.ksp.get().pluginId)
    id(libs.plugins.kover.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.repository"
}

dependencies {
    // region Private
    implementation(project(":core:mediator"))
    api(project(":data:entity"))
    implementation(project(":data:database"))
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    // endregion

    // region Kotlin
    api(libs.kotlinx.coroutines)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
