package com.erdenian.studentassistant.homeworks.api

import kotlinx.serialization.Serializable

@Serializable
data class HomeworkEditorRoute(val semesterId: Long, val homeworkId: Long? = null, val subjectName: String? = null)
