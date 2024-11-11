package com.erdenian.studentassistant.homeworks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.erdenian.studentassistant.homeworks.api.HomeworksApi
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.homeworks.di.HomeworksComponentHolder
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import javax.inject.Inject

public fun createHomeworksApi(dependencies: HomeworksDependencies): HomeworksApi =
    HomeworksComponentHolder.create(dependencies).api

internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override fun addToGraph(builder: NavGraphBuilder) {
        builder.composable<HomeworksRoute.Homeworks> { HomeworksScreen() }
        builder.composable<HomeworksRoute.HomeworkEditor> { HomeworkEditorScreen(it.toRoute()) }
    }
}
