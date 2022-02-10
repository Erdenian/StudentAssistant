plugins {
    id("com.android.library")
    kotlin("android")
}

dependencies {
    // region Tests
    val junitVersion: String by project
    testImplementation("junit:junit:$junitVersion")
    // endregion
}
