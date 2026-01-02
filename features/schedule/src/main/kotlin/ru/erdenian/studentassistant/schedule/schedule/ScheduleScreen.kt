package ru.erdenian.studentassistant.schedule.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.schedule.di.ScheduleComponentHolder

@Composable
internal fun ScheduleScreen() {
    val viewModel = viewModel { ScheduleComponentHolder.instance.scheduleViewModel }
    val navController = LocalNavigator.current

    val semesters by viewModel.allSemesters.collectAsState()
    val semestersNames by remember { derivedStateOf { semesters.map { it.name } } }
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    @Suppress("Wrapping")
    val rememberLessons = remember<@Composable (date: LocalDate) -> State<List<Lesson>?>>(viewModel) {
        { date ->
            produceState(null, date) {
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
