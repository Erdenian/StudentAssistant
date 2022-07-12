plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.erdenian.studentassistant.style"

    buildFeatures.compose = true
}

dependencies {
    // region Compose
    api(libsAndroidx.bundles.compose)
    //endregion
}
