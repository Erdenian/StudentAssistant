package com.erdenian.studentassistant.homeworks.api

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class HomeworkScreen : ScreenProvider {

    data object Homeworks : HomeworkScreen()

    data class HomeworkEditor internal constructor(
        val semesterId: Long,
        val homeworkId: Long? = null,
        val subjectName: String? = null
    ) : HomeworkScreen() {

        constructor(semesterId: Long, subjectName: String? = null) :
                this(semesterId, homeworkId = null, subjectName = subjectName)

        constructor(semesterId: Long, homeworkId: Long) :
                this(semesterId, homeworkId = homeworkId, subjectName = null)
    }
}
