plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":data:repository"))
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    // endregion

    // region AndroidX
    val lifecycleVersion: String by project
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // endregion

    // region Core
    val kodeinVersion: String by project
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")
    // endregion
}
