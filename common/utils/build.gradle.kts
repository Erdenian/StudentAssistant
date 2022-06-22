plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.erdenian.studentassistant.utils"
}

dependencies {
    // region Tests
    val junitVersion: String by project
    testImplementation("junit:junit:$junitVersion")
    // endregion
}
