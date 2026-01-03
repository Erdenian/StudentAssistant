plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "ru.erdenian.studentassistant.analytics"
}

dependencies {
    // region Private
    implementation(project(":features:analytics:api"))
    implementation(project(":features:analytics:firebase"))
    debugImplementation(project(":features:analytics:logcat"))

    implementation(project(":common:utils"))
    // endregion

    // region AndroidX
    implementation(libs.androidx.core.ktx)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
