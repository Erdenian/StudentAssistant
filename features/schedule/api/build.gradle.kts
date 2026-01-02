plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.erdenian.studentassistant.schedule.api"
}

dependencies {
    // region Private
    implementation(project(":common:navigation"))
    implementation(project(":features:repository:api"))
    // endregion

    // region Kotlin
    implementation(libs.kotlinx.serialization)
    // endregion

    // region AndroidX
    implementation(libs.androidx.navigation3.runtime)
    // endregion
}
