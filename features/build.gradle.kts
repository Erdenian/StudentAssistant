subprojects {
    afterEvaluate {
        dependencies {
            val implementation by configurations
            implementation(project(":common:strings"))
        }
    }
}
