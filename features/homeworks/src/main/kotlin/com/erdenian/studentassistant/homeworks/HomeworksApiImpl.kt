package ru.erdenian.studentassistant.homeworks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import javax.inject.Inject
import ru.erdenian.studentassistant.homeworks.api.HomeworksApi
import ru.erdenian.studentassistant.homeworks.api.HomeworksRoute
import ru.erdenian.studentassistant.homeworks.di.HomeworksComponentHolder
import ru.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import ru.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import ru.erdenian.studentassistant.navigation.composableAnimated

public fun createHomeworksApi(dependencies: HomeworksDependencies): HomeworksApi =
    HomeworksComponentHolder.create(dependencies).api

internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override fun addToGraph(builder: NavGraphBuilder) {
        builder.composableAnimated<HomeworksRoute.Homeworks> { HomeworksScreen() }
        builder.composableAnimated<HomeworksRoute.HomeworkEditor> { HomeworkEditorScreen(it.toRoute()) }
    }
}
