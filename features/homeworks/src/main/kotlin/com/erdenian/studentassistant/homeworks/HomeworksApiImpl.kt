package com.erdenian.studentassistant.homeworks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.erdenian.studentassistant.homeworks.api.HomeworksApi
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.homeworks.di.HomeworksComponentHolder
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import com.erdenian.studentassistant.navigation.composableAnimated
import javax.inject.Inject

public fun createHomeworksApi(dependencies: HomeworksDependencies): HomeworksApi =
    HomeworksComponentHolder.create(dependencies).api

internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override fun addToGraph(builder: NavGraphBuilder) {
        builder.composableAnimated<HomeworksRoute.Homeworks> { HomeworksScreen() }
        builder.composableAnimated<HomeworksRoute.HomeworkEditor> { HomeworkEditorScreen(it.toRoute()) }
    }
}
