plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.erdenian.studentassistant.utils"
}

dependencies {
    // region Tests
    testImplementation(libsTest.junit)
    // endregion
}
