plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.erdenian.studentassistant.uikit"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":core:strings"))
    implementation(project(":core:style"))
    // endregion

    // region UI
    implementation(libsUi.accompanist.placeholder)
    // endregion
}
