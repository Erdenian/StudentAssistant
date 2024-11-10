plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.erdenian.studentassistant.settings.api"
}

dependencies {
    implementation(project(":common:navigation"))
    implementation(libs.kotlinx.serialization)
}
