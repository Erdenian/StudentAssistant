package com.erdenian.studentassistant.homeworks.api

import com.erdenian.studentassistant.navigation.Route
import kotlinx.serialization.Serializable

sealed interface HomeworksRoute : Route {

    @Serializable
    data object Homeworks : HomeworksRoute

    @Serializable
    data class HomeworkEditor(
        val semesterId: Long,
        val homeworkId: Long? = null,
        val subjectName: String? = null,
    ) : HomeworksRoute
}
