package ru.erdenian.studentassistant.schedule

import android.os.Bundle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.toRoute
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlinx.serialization.json.Json
import ru.erdenian.studentassistant.navigation.composableAnimated
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.api.ScheduleApi
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import ru.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import ru.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import ru.erdenian.studentassistant.schedule.schedule.ScheduleScreen
import ru.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import ru.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen
import ru.erdenian.studentassistant.utils.getParcelableCompat

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
                    override fun get(bundle: Bundle, key: String) = bundle.getParcelableCompat<Lesson>(key)
                    override fun serializeAsValue(value: Lesson) = Json.encodeToString(value)
                    override fun parseValue(value: String) = Json.decodeFromString<Lesson>(value)
                },
            ),
        ) { LessonInformationScreen(it.toRoute()) }
    }
}
