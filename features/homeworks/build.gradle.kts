plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    compileOnly(project(":common:sampledata"))

    implementation(project(":data:repository"))
    // endregion

    // region AndroidX
    val lifecycleVersion: String by project
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // endregion

    // region Core
    val daggerVersion: String by project
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    // endregion

    // region UI
    val accompanistVersion: String by project
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    // endregion
}
