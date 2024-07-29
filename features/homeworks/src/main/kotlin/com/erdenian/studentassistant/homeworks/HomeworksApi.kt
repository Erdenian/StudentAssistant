package com.erdenian.studentassistant.homeworks

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.erdenian.studentassistant.homeworks.api.HomeworkScreen
import com.erdenian.studentassistant.homeworks.di.DaggerHomeworksComponent
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import com.erdenian.studentassistant.mediator.ApiComponentHolder
import javax.inject.Inject

interface HomeworksApi {
    val screenModule: ScreenRegistry.() -> Unit
}

internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override val screenModule = screenModule {
        register<HomeworkScreen.Homeworks> { HomeworksScreen() }
        register<HomeworkScreen.HomeworkEditor> { HomeworkEditorScreen(it) }
    }
}

class HomeworksApiComponentHolder(dependencies: HomeworksDependencies) : ApiComponentHolder<HomeworksApi>(
    DaggerHomeworksComponent.factory().create(dependencies)
)
