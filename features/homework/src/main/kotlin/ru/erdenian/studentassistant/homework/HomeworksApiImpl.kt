package ru.erdenian.studentassistant.homework

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.homework.api.HomeworksApi
import ru.erdenian.studentassistant.homework.api.HomeworksRoute
import ru.erdenian.studentassistant.homework.di.HomeworksComponentHolder
import ru.erdenian.studentassistant.homework.homeworkeditor.HomeworkEditorScreen
import ru.erdenian.studentassistant.homework.homeworks.HomeworksScreen
import ru.erdenian.studentassistant.navigation.NavGraphContributor

public fun createHomeworksApi(dependencies: HomeworksDependencies): HomeworksApi =
    HomeworksComponentHolder.create(dependencies).api

@Singleton
internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override val navGraphContributor = object : NavGraphContributor {
        override fun addTo(scope: EntryProviderScope<NavKey>) {
            scope.entry<HomeworksRoute.Homeworks> { HomeworksScreen() }
            scope.entry<HomeworksRoute.HomeworkEditor> { HomeworkEditorScreen(it) }
        }
    }
}
