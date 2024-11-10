package com.erdenian.studentassistant.schedule.api

import kotlinx.serialization.Serializable

@Serializable
data class SemesterEditorRoute(val semesterId: Long? = null)
