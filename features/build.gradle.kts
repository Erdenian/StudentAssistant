val featuresProject = project
subprojects {
    if (parent !== featuresProject) return@subprojects
    afterEvaluate {
        dependencies {
            val implementation by configurations
            implementation(project(":core:strings"))
            implementation(project(":core:style"))
        }
    }
}
