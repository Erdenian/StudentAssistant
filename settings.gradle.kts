rootProject.name = "StudentAssistant"

include(
    ":app",

    ":features:schedule",
    ":features:homeworks",
    ":features:settings",

    ":common:uikit",
    ":common:utils",
    ":common:sampledata",

    ":data:repository",
    ":data:database",
    ":data:entity",

    ":core:strings",
    ":core:style"
)
