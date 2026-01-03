plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "ru.erdenian.studentassistant.analytics.firebase"
}

dependencies {
    // region Private
    implementation(project(":features:analytics:api"))
    // endregion

    // region Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
