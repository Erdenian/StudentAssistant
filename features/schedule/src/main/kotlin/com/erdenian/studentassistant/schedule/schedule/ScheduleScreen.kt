package com.erdenian.studentassistant.schedule.schedule

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.entity.Semester
import com.erdenian.studentassistant.sampledata.Lessons
import com.erdenian.studentassistant.sampledata.Semesters
import com.erdenian.studentassistant.schedule.composable.LazyLessonsList
import com.erdenian.studentassistant.schedule.composable.PagerTabStrip
import com.erdenian.studentassistant.schedule.schedule.SemesterWithState.Companion.animateScrollToDate
import com.erdenian.studentassistant.schedule.schedule.SemesterWithState.Companion.currentDate
import com.erdenian.studentassistant.schedule.schedule.SemesterWithState.Companion.getDate
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import com.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu
import com.erdenian.studentassistant.utils.showDatePicker
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
        onAddSemesterClick = navigateToAddSemester,
        onEditSemesterClick = { navigateToEditSchedule(it.id) },
        onLessonClick = { navigateToShowLessonInformation(it.id) }
    )
}

@Stable
private data class SemesterWithState(val semester: Semester, val pagerState: PagerState) {

    constructor(semester: Semester, initialDate: LocalDate) : this(semester, PagerState(semester.getPosition(initialDate)))

    companion object {
        val SemesterWithState.currentDate get() = getDate(pagerState.currentPage)
        fun SemesterWithState.getDate(page: Int) = semester.getDate(page)
        suspend fun SemesterWithState.animateScrollToDate(date: LocalDate) =
            pagerState.animateScrollToPage(semester.getPosition(date))

        private fun Semester.getDate(position: Int): LocalDate = firstDay.plusDays(position.toLong())
        private fun Semester.getPosition(date: LocalDate) = ChronoUnit.DAYS.between(firstDay, date.coerceIn(range)).toInt()
    }
}

@Composable
private fun ScheduleContent(
    semestersNames: List<String>,
    selectedSemester: Semester?,
    rememberLessons: @Composable (date: LocalDate) -> State<List<Lesson>?>,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddSemesterClick: () -> Unit,
    onEditSemesterClick: (semester: Semester) -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    var currentDate: LocalDate? by rememberSaveable { mutableStateOf(null) }

    val state = remember(selectedSemester) {
        if (selectedSemester == null) null
        else SemesterWithState(selectedSemester, currentDate ?: LocalDate.now())
    }

    LaunchedEffect(state) {
        snapshotFlow { state?.currentDate }.collect { currentDate = it }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
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
                val pageCount = state.semester.length

                PagerTabStrip(
                    count = pageCount,
                    state = state.pagerState,
                    titleGetter = { page ->
                        val date = state.semester.firstDay.plusDays(page.toLong())
                        date.format(if (date.year == LocalDate.now().year) shortTitleFormatter else fullTitleFormatter)
                    }
                )

                HorizontalPager(
                    count = pageCount,
                    state = state.pagerState,
                    key = { state.semester.id to state.getDate(it) },
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val lessons by rememberLessons(state.getDate(page))
                    LazyLessonsList(lessons = lessons, onLessonClick = onLessonClick)
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
        rememberLessons = { remember { mutableStateOf(emptyList()) } },
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
        rememberLessons = { remember { mutableStateOf(null) } },
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
        rememberLessons = { remember { mutableStateOf(emptyList()) } },
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
        rememberLessons = { remember { mutableStateOf(lessons) } },
        onSelectedSemesterChange = {},
        onAddSemesterClick = {},
        onEditSemesterClick = {},
        onLessonClick = {}
    )
}
