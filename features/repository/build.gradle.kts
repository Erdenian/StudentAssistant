plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "ru.erdenian.studentassistant.repository"

    testOptions.unitTests.all { it.jvmArgs("--add-opens=java.base/java.time=ALL-UNNAMED") }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

dependencies {
    // region Private
    implementation(project(":features:repository:api"))
    implementation(project(":common:utils"))
    // endregion

    // region Tests
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    // endregion

    // region Kotlin
    implementation(libs.kotlinx.coroutines)
    // endregion

    // region AndroidX
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room)
    // endregion

    // region Core
    ksp(libs.core.dagger.compiler)
    implementation(libs.core.dagger)
    // endregion
}
