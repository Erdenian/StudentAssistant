package ru.erdenian.studentassistant.schedule.schedule

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.schedule.composable.LazyLessonsList
import ru.erdenian.studentassistant.schedule.composable.PagerTabStrip
import ru.erdenian.studentassistant.schedule.schedule.State.Companion.animateScrollToDate
import ru.erdenian.studentassistant.schedule.schedule.State.Companion.currentDate
import ru.erdenian.studentassistant.schedule.schedule.State.Companion.getDate
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu
import ru.erdenian.studentassistant.utils.showDatePicker

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    navigateToAddSemester: () -> Unit,
    navigateToEditSchedule: (semesterId: Long) -> Unit,
    navigateToShowLessonInformation: (lessonId: Long) -> Unit
) {
    val semesters by viewModel.allSemesters.collectAsState()
    val semestersNames by remember { derivedStateOf { semesters.map { it.name } } }
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    val lessonsGetter = remember<(LocalDate) -> Flow<List<Lesson>>>(viewModel) {
        { date -> viewModel.getLessons(date).map { it.list } }
    }

    ScheduleContent(
        semestersNames = semestersNames,
        selectedSemester = selectedSemester,
        lessonsGetter = lessonsGetter,
        onSelectedSemesterChange = { index -> viewModel.selectSemester(semesters.list[index].id) },
        onAddSemesterClick = navigateToAddSemester,
        onEditSemesterClick = { navigateToEditSchedule(it.id) },
        onLessonClick = { navigateToShowLessonInformation(it.id) }
    )
}

@Stable
private data class State(val pagerState: PagerState, val semester: Semester) {

    constructor(semester: Semester, initialDate: LocalDate) : this(PagerState(semester.getPosition(initialDate)), semester)

    companion object {
        val State.currentDate get() = getDate(pagerState.currentPage)
        fun State.getDate(page: Int) = semester.getDate(page)
        suspend fun State.animateScrollToDate(date: LocalDate) = pagerState.animateScrollToPage(semester.getPosition(date))

        private fun Semester.getDate(position: Int): LocalDate = firstDay.plusDays(position.toLong())
        private fun Semester.getPosition(date: LocalDate) = ChronoUnit.DAYS.between(firstDay, date.coerceIn(range)).toInt()
    }
}

@Composable
private fun ScheduleContent(
    semestersNames: List<String>,
    selectedSemester: Semester?,
    lessonsGetter: (date: LocalDate) -> Flow<List<Lesson>>,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddSemesterClick: () -> Unit,
    onEditSemesterClick: (semester: Semester) -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    var currentDate: LocalDate? by rememberSaveable { mutableStateOf(null) }

    val state = remember(selectedSemester) {
        if (selectedSemester == null) null
        else State(selectedSemester, currentDate ?: LocalDate.now())
    }

    currentDate = state?.currentDate

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if ((selectedSemester == null) || (semestersNames.size <= 1)) {
                        Text(text = stringResource(RS.s_title))
                    } else {
                        TopAppBarDropdownMenu(
                            items = semestersNames,
                            selectedItem = selectedSemester.name,
                            onSelectedItemChange = { index, _ -> onSelectedSemesterChange(index) }
                        )
                    }
                },
                actions = {
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()
                    TopAppBarActions(
                        actions = listOfNotNull(
                            if (state != null) {
                                ActionItem.AlwaysShow(
                                    name = stringResource(RS.s_calendar),
                                    imageVector = AppIcons.Today,
                                    onClick = {
                                        context.showDatePicker(
                                            state.currentDate,
                                            state.semester.firstDay,
                                            state.semester.lastDay
                                        ) { date ->
                                            coroutineScope.launch { state.animateScrollToDate(date) }
                                        }
                                    }
                                )
                            } else null,
                            if (selectedSemester == null) {
                                ActionItem.AlwaysShow(
                                    name = stringResource(RS.s_add),
                                    imageVector = AppIcons.Add,
                                    onClick = onAddSemesterClick
                                )
                            } else {
                                ActionItem.NeverShow(
                                    name = stringResource(RS.s_add),
                                    onClick = onAddSemesterClick
                                )
                            },
                            if (selectedSemester != null) {
                                ActionItem.NeverShow(
                                    name = stringResource(RS.s_edit),
                                    onClick = { onEditSemesterClick(selectedSemester) }
                                )
                            } else null
                        )
                    )
                }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (state == null) {
                Text(
                    text = stringResource(RS.s_no_schedule),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
                )
            } else {
                val shortTitleFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM") }
                val fullTitleFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy") }

                PagerTabStrip(
                    state = state.pagerState,
                    titleGetter = { page ->
                        val date = state.semester.firstDay.plusDays(page.toLong())
                        date.format(if (date.year == LocalDate.now().year) shortTitleFormatter else fullTitleFormatter)
                    }
                )

                key(state.semester) {
                    HorizontalPager(
                        count = state.semester.length,
                        state = state.pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val lessonsFlow = remember(lessonsGetter, state, page) { lessonsGetter(state.getDate(page)) }
                        val lessons by lessonsFlow.collectAsState(null)

                        LazyLessonsList(lessons = lessons, onLessonClick = onLessonClick)
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleScreenNoSchedulePreview() = AppTheme {
    ScheduleContent(
        semestersNames = emptyList(),
        selectedSemester = null,
        lessonsGetter = { flowOf(emptyList()) },
        onSelectedSemesterChange = {},
        onAddSemesterClick = {},
        onEditSemesterClick = {},
        onLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleScreenLoadingPreview() = AppTheme {
    ScheduleContent(
        semestersNames = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular,
        lessonsGetter = { flow {} },
        onSelectedSemesterChange = {},
        onAddSemesterClick = {},
        onEditSemesterClick = {},
        onLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleScreenNoLessonsPreview() = AppTheme {
    ScheduleContent(
        semestersNames = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular,
        lessonsGetter = { flowOf(emptyList()) },
        onSelectedSemesterChange = {},
        onAddSemesterClick = {},
        onEditSemesterClick = {},
        onLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleScreenPreview() = AppTheme {
    val lessons = List(10) { Lessons.regular }
    ScheduleContent(
        semestersNames = listOf(Semesters.regular.name, Semesters.long.name),
        selectedSemester = Semesters.regular,
        lessonsGetter = { flowOf(lessons) },
        onSelectedSemesterChange = {},
        onAddSemesterClick = {},
        onEditSemesterClick = {},
        onLessonClick = {}
    )
}
