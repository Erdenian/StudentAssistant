plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    buildFeatures.compose = true
    namespace = "com.erdenian.studentassistant.sampledata"
}

dependencies {
    // region Private
    implementation(project(":core:style"))
    implementation(project(":data:entity"))
    // endregion
}
