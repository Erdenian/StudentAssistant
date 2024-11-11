plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.erdenian.studentassistant.homeworks.api"
}

dependencies {
    // region Private
    implementation(project(":common:navigation"))
    // endregion

    // region Kotlin
    implementation(libs.kotlinx.serialization)
    // endregion
}
