plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    buildFeatures.compose = true
}

dependencies {
    // region Compose
    val composeVersion: String by project

    api("androidx.compose.ui:ui:$composeVersion")
    api("androidx.compose.ui:ui-tooling:$composeVersion")

    api("androidx.compose.foundation:foundation:$composeVersion")
    api("androidx.compose.material:material:$composeVersion")

    api("androidx.compose.material:material-icons-core:$composeVersion")
    api("androidx.compose.material:material-icons-extended:$composeVersion")
    //endregion
}
