plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.erdenian.studentassistant.schedule"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    implementation(project(":common:sampledata"))

    implementation(project(":data:repository"))
    // endregion

    // region AndroidX
    implementation(libsAndroidx.lifecycle.viewmodel)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion

    // region UI
    implementation(libsUi.accompanist.placeholder)
    implementation(libsUi.accompanist.pager)
    // endregion
}
