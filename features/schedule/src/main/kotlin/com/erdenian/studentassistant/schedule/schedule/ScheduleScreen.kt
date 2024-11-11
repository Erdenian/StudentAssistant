package com.erdenian.studentassistant.schedule.schedule

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdenian.studentassistant.navigation.LocalNavController
import com.erdenian.studentassistant.repository.api.entity.Lesson
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import java.time.LocalDate

@Composable
internal fun ScheduleScreen() {
    val viewModel = viewModel { ScheduleComponentHolder.instance.scheduleViewModel }
    val navController = LocalNavController.current

    val semesters by viewModel.allSemesters.collectAsState()
    val semestersNames by remember { derivedStateOf { semesters.map { it.name } } }
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    @Suppress("Wrapping")
    val rememberLessons = remember<@Composable (date: LocalDate) -> State<List<Lesson>?>>(viewModel) {
        { date ->
            // https://issuetracker.google.com/issues/368420773
            @SuppressLint("ProduceStateDoesNotAssignValue")
            produceState<List<Lesson>?>(null, date) {
                viewModel.getLessons(date).collect { value = it }
            }
        }
    }

    ScheduleContent(
        semestersNames = semestersNames,
        selectedSemester = selectedSemester,
        rememberLessons = rememberLessons,
        onSelectedSemesterChange = { index -> viewModel.selectSemester(semesters[index].id) },
        onAddSemesterClick = { navController.navigate(ScheduleRoute.SemesterEditor()) },
        onEditScheduleClick = { navController.navigate(ScheduleRoute.ScheduleEditor(semesterId = it.id)) },
        onLessonClick = { navController.navigate(ScheduleRoute.LessonInformation(lesson = it)) },
    )
}
