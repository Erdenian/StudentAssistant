package ru.erdenian.studentassistant.homeworks

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.erdenian.studentassistant.homeworks.api.HomeworksApi
import ru.erdenian.studentassistant.homeworks.api.HomeworksRoute
import ru.erdenian.studentassistant.homeworks.di.HomeworksComponentHolder
import ru.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import ru.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen

public fun createHomeworksApi(dependencies: HomeworksDependencies): HomeworksApi =
    HomeworksComponentHolder.create(dependencies).api

internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override fun addToGraph(scope: EntryProviderScope<NavKey>) {
        scope.entry<HomeworksRoute.Homeworks> { HomeworksScreen() }
        scope.entry<HomeworksRoute.HomeworkEditor> { HomeworkEditorScreen(it) }
    }
}
