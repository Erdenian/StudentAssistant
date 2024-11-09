package com.erdenian.studentassistant.schedule.schedule

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.erdenian.studentassistant.entity.Lesson
import java.time.LocalDate
import kotlinx.coroutines.flow.map

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    navigateToAddSemester: () -> Unit,
    navigateToEditSchedule: (semesterId: Long) -> Unit,
    navigateToShowLessonInformation: (lessonId: Long) -> Unit,
) {
    val semesters by viewModel.allSemesters.collectAsState()
    val semestersNames by remember { derivedStateOf { semesters.map { it.name } } }
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    val rememberLessons = remember<@Composable (date: LocalDate) -> State<List<Lesson>?>>(viewModel) {
        { date ->
            // https://issuetracker.google.com/issues/368420773
            @SuppressLint("ProduceStateDoesNotAssignValue")
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
        onAddSemesterClick = navigateToAddSemester,
        onEditSemesterClick = { navigateToEditSchedule(it.id) },
        onLessonClick = { navigateToShowLessonInformation(it.id) },
    )
}
