plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    buildFeatures.compose = true
}

dependencies {
    // region Private
    implementation(project(":core:strings"))
    implementation(project(":core:style"))
    // endregion

    // region UI
    val materialVersion: String by project
    api("com.google.android.material:material:$materialVersion")

    val accompanistVersion: String by project
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    // endregion
}
