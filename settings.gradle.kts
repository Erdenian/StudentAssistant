rootProject.name = "StudentAssistant"

include(
    ":app",

    ":features:settings",

    ":data:repository",
    ":data:database",
    ":data:entity",

    ":common:strings",
    ":common:uikit",
    ":common:utils"
)
