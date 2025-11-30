plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.erdenian.studentassistant.settings"

    testOptions.unitTests.all { it.jvmArgs("--add-opens=java.base/java.time=ALL-UNNAMED") }
}

dependencies {
    // region Private
    implementation(project(":common:uikit"))
    implementation(project(":common:utils"))
    implementation(project(":common:navigation"))

    implementation(project(":features:repository:api"))
    implementation(project(":features:settings:api"))
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    // endregion

    // region AndroidX
    implementation(libs.androidx.lifecycle.viewmodel)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
