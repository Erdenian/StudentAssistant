package ru.erdenian.studentassistant.homeworks.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface HomeworksRoute : NavKey {

    @Serializable
    data object Homeworks : HomeworksRoute

    @Serializable
    data class HomeworkEditor(
        val semesterId: Long,
        val homeworkId: Long? = null,
        val subjectName: String? = null,
    ) : HomeworksRoute
}
