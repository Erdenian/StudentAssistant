rootProject.name = "StudentAssistant"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.60.6"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("config") { from(files("gradle/config.versions.toml")) }
    }
}

refreshVersions {
    rejectVersionIf { candidate.stabilityLevel.isLessStableThan(current.stabilityLevel) }

    versionsPropertiesFile = file("build/versions.properties").apply { parentFile.mkdirs(); createNewFile() }
    gradle.rootProject {
        val versionsFile = file("gradle/libs.versions.toml")
        tasks.configureEach {
            if ((name != "refreshVersions") && (name != "refreshVersionsCleanup")) return@configureEach

            lateinit var blockOrder: List<String>
            lateinit var prefix: String

            doFirst {
                val originalContent = versionsFile.readText()
                blockOrder = """(?m)^\[(\w+)\]""".toRegex().findAll(originalContent).map { it.groupValues[1] }.toList()
                prefix = originalContent.take(originalContent.indexOf('[').takeIf { it != -1 } ?: 0)
            }

            doLast {
                val ls = System.lineSeparator()
                val blocks = mutableMapOf<String, String>()
                var currentName = ""
                var currentBlock = ""

                versionsFile.readText().replace("⬆", " ⬆").lineSequence().forEach { line ->
                    when {
                        line.matches("""^\[\w+\]""".toRegex()) -> {
                            currentName = line.removeSurrounding("[", "]")
                            currentBlock = line
                        }
                        currentName.isNotEmpty() -> currentBlock += ls + line
                        else -> Unit
                    }
                    if (currentName.isNotEmpty()) blocks[currentName] = currentBlock
                }

                versionsFile.writeText(
                    prefix + blockOrder.joinToString(ls.repeat(2)) { block ->
                        blocks[block]?.trimEnd() ?: "[$block]"
                    } + ls,
                )
            }
        }
    }
}

include(
    ":app",

    ":features:repository",
    ":features:repository:api",
    ":features:schedule",
    ":features:schedule:api",
    ":features:homeworks",
    ":features:homeworks:api",
    ":features:settings",
    ":features:settings:api",

    ":common:navigation",
    ":common:uikit",
    ":common:utils",
    ":common:sampledata",

    ":core:strings",
    ":core:style",
)
