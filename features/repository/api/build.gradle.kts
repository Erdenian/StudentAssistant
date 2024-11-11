plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.erdenian.studentassistant.repository.api"
}

dependencies {
    // region Private
    api(project(":common:entity"))
    // endregion

    // region Kotlin
    api(libs.kotlinx.coroutines)
    // endregion
}
