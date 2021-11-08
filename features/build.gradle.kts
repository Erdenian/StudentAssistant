subprojects {
    afterEvaluate {
        dependencies {
            val implementation by configurations
            implementation(project(":core:strings"))
            implementation(project(":core:style"))
            implementation(project(":core:sampledata"))
        }
    }
}
