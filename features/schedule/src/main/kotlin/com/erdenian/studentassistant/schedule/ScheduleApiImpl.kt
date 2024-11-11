package com.erdenian.studentassistant.schedule

import android.os.Bundle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.toRoute
import com.erdenian.studentassistant.navigation.composableAnimated
import com.erdenian.studentassistant.repository.api.entity.Lesson
import com.erdenian.studentassistant.schedule.api.ScheduleApi
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import com.erdenian.studentassistant.schedule.schedule.ScheduleScreen
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public fun createScheduleApi(dependencies: ScheduleDependencies): ScheduleApi =
    ScheduleComponentHolder.create(dependencies).api

internal class ScheduleApiImpl @Inject constructor() : ScheduleApi {
    override fun addToGraph(builder: NavGraphBuilder) {
        builder.composableAnimated<ScheduleRoute.Schedule> { ScheduleScreen() }
        builder.composableAnimated<ScheduleRoute.SemesterEditor> { SemesterEditorScreen(it.toRoute()) }
        builder.composableAnimated<ScheduleRoute.ScheduleEditor> { ScheduleEditorScreen(it.toRoute()) }
        builder.composableAnimated<ScheduleRoute.LessonEditor> { LessonEditorScreen(it.toRoute()) }
        builder.composableAnimated<ScheduleRoute.LessonInformation>(
            typeMap = mapOf(
                typeOf<Lesson>() to object : NavType<Lesson>(isNullableAllowed = false) {
                    override fun put(bundle: Bundle, key: String, value: Lesson) = bundle.putParcelable(key, value)
                    override fun get(bundle: Bundle, key: String) = bundle.getParcelable<Lesson>(key)
                    override fun serializeAsValue(value: Lesson) = Json.encodeToString(value)
                    override fun parseValue(value: String) = Json.decodeFromString<Lesson>(value)
                },
            ),
        ) { LessonInformationScreen(it.toRoute()) }
    }
}
