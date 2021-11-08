rootProject.name = "StudentAssistant"

include(
    ":app",

    ":features:settings",

    ":data:repository",
    ":data:database",
    ":data:entity",

    ":core:strings",
    ":core:style",

    ":common:uikit",
    ":common:utils"
)
