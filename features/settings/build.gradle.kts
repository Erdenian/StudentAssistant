plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.erdenian.studentassistant.settings"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":data:repository"))
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    // endregion

    // region AndroidX
    implementation(libsAndroidx.lifecycle.viewmodel)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion
}
