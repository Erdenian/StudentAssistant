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
    val junitVersion: String by project
    testImplementation("junit:junit:$junitVersion")

    val junitKtxVersion: String by project
    androidTestImplementation("androidx.test.ext:junit-ktx:$junitKtxVersion")

    val androidxTestVersion: String by project
    androidTestImplementation("androidx.test:core-ktx:$androidxTestVersion")
    androidTestImplementation("androidx.test:runner:$androidxTestVersion")
    // endregion

    // region Kotlin
    val coroutinesVersion: String by project
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    // endregion

    // region Core
    val daggerVersion: String by project
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    // endregion
}
