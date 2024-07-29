package com.erdenian.studentassistant.schedule.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.mediator.findComponent
import com.erdenian.studentassistant.schedule.ScheduleApi
import com.erdenian.studentassistant.schedule.api.ScheduleScreen
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import java.time.LocalDate
import kotlinx.coroutines.flow.map

internal class ScheduleScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel { findComponent<ScheduleApi, ScheduleComponent>().scheduleViewModel }
        val navigator = LocalNavigator.currentOrThrow

        val semesters by viewModel.allSemesters.collectAsState()
        val semestersNames by remember { derivedStateOf { semesters.map { it.name } } }
        val selectedSemester by viewModel.selectedSemester.collectAsState()

        val rememberLessons = remember<@Composable (date: LocalDate) -> State<List<Lesson>?>>(viewModel) {
            { date ->
                produceState<List<Lesson>?>(null, date) {
                    viewModel.getLessons(date).map { it.list }.collect { value = it }
                }
            }
        }

        ScheduleContent(
            semestersNames = semestersNames,
            selectedSemester = selectedSemester,
            rememberLessons = rememberLessons,
            onSelectedSemesterChange = { index -> viewModel.selectSemester(semesters.list[index].id) },
            onAddSemesterClick = { navigator.push(ScreenRegistry.get(ScheduleScreen.SemesterEditor())) },
            onEditSemesterClick = { navigator.push(ScreenRegistry.get(ScheduleScreen.SemesterEditor(it.id))) },
            onLessonClick = { navigator.push(ScreenRegistry.get(ScheduleScreen.LessonInformation(it.id))) }
        )
    }
}
