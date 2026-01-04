package ru.erdenian.studentassistant.homework

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.homework.api.HomeworkApi
import ru.erdenian.studentassistant.homework.api.HomeworkRoute
import ru.erdenian.studentassistant.homework.di.HomeworkComponentHolder
import ru.erdenian.studentassistant.homework.homeworkeditor.HomeworkEditorScreen
import ru.erdenian.studentassistant.homework.homeworks.HomeworksScreen
import ru.erdenian.studentassistant.navigation.NavGraphContributor

public fun createHomeworkApi(dependencies: HomeworkDependencies): HomeworkApi =
    HomeworkComponentHolder.create(dependencies).api

@Singleton
internal class HomeworkApiImpl @Inject constructor() : HomeworkApi {
    override val navGraphContributor = object : NavGraphContributor {
        override fun addTo(scope: EntryProviderScope<NavKey>) {
            scope.entry<HomeworkRoute.Homeworks> { HomeworksScreen() }
            scope.entry<HomeworkRoute.HomeworkEditor> { HomeworkEditorScreen(it) }
        }
    }
}
