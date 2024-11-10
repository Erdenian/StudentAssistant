val featuresProject = project
subprojects {
    if (parent !== featuresProject) return@subprojects

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xexplicit-api=strict")
        }
    }

    afterEvaluate {
        dependencies {
            if (project.plugins.hasPlugin(libs.plugins.kotlin.compose.get().pluginId)) {
                "implementation"(project(":core:strings"))
                "implementation"(project(":core:style"))
            }
        }
    }
}
