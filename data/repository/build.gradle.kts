plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.erdenian.studentassistant.repository"
}

dependencies {
    // region Private
    api(project(":data:entity"))
    implementation(project(":data:database"))
    // endregion

    // region Tests
    testImplementation(libsTest.junit)
    androidTestImplementation(libsTest.bundles.android)
    // endregion

    // region Kotlin
    api(libsKotlinx.coroutines)
    // endregion

    // region Core
    kapt(libsCore.dagger.compiler)
    implementation(libsCore.dagger)
    // endregion
}
