package ru.erdenian.studentassistant.homework.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface HomeworkRoute : NavKey {

    @Serializable
    data object Homeworks : HomeworkRoute

    @Serializable
    data class HomeworkEditor(
        val semesterId: Long,
        val homeworkId: Long? = null,
        val subjectName: String? = null,
    ) : HomeworkRoute
}
