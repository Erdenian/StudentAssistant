plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.erdenian.studentassistant.sampledata"

    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":core:style"))
    implementation(project(":data:entity"))
    // endregion
}
