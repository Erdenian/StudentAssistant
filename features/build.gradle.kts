val thisProject = project
subprojects {
    if (project.parent !== thisProject) return@subprojects
    afterEvaluate {
        dependencies {
            val implementation by configurations
            implementation(project(":core:strings"))
            implementation(project(":core:style"))
        }
    }
}
