rootProject.name = "StudentAssistant"

include(
    ":app",

    ":features:schedule",
    ":features:homeworks",
    ":features:settings",

    ":common:uikit",
    ":common:utils",

    ":core:strings",
    ":core:style",
    ":core:sampledata",

    ":data:repository",
    ":data:database",
    ":data:entity"
)
