plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.erdenian.studentassistant.schedule"

    testOptions.unitTests.all { it.jvmArgs("--add-opens=java.base/java.time=ALL-UNNAMED") }
}

dependencies {
    // region Private
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    implementation(project(":common:sampledata"))
    implementation(project(":common:navigation"))

    implementation(project(":features:repository:api"))
    implementation(project(":features:schedule:api"))
    implementation(project(":features:homeworks:api"))
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    // endregion

    // region Kotlin
    implementation(libs.kotlinx.serialization)
    // endregion

    // region AndroidX
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation3.ui)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
