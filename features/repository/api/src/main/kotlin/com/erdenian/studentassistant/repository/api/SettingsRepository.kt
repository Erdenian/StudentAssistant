package com.erdenian.studentassistant.repository.api

import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    var defaultStartTime: LocalTime
    fun getDefaultStartTimeFlow(scope: CoroutineScope): StateFlow<LocalTime>

    var defaultLessonDuration: Duration
    fun getDefaultLessonDurationFlow(scope: CoroutineScope): StateFlow<Duration>

    var defaultBreakDuration: Duration
    fun getDefaultBreakDurationFlow(scope: CoroutineScope): StateFlow<Duration>

    var isAdvancedWeeksSelectorEnabled: Boolean
    fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope): StateFlow<Boolean>
}
