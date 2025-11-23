package ru.erdenian.studentassistant.homeworks.api

import kotlinx.serialization.Serializable
import ru.erdenian.studentassistant.navigation.Route

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
