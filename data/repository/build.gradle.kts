@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id(libsPlugins.plugins.android.library.get().pluginId)
    id(libsPlugins.plugins.kotlin.android.get().pluginId)
    id(libsPlugins.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "com.erdenian.studentassistant.repository"
}

dependencies {
    // region Private
    api(project(":data:entity"))
    implementation(project(":data:database"))
    // endregion

    // region Tests
    testImplementation(libsTest.junit)
    androidTestImplementation(libsTest.bundles.android)
    // endregion

    // region Kotlin
    api(libsKotlinx.coroutines)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion
}
