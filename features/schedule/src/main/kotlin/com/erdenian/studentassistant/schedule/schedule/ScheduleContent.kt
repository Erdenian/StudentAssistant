package ru.erdenian.studentassistant.schedule.schedule

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.schedule.composable.LazyLessonsList
import ru.erdenian.studentassistant.schedule.composable.PagerTabStrip
import ru.erdenian.studentassistant.schedule.schedule.SemesterWithState.Companion.animateScrollToDate
import ru.erdenian.studentassistant.schedule.schedule.SemesterWithState.Companion.currentDate
import ru.erdenian.studentassistant.schedule.schedule.SemesterWithState.Companion.getDate
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.dialog.DatePickerDialog
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu

@Composable
internal fun ScheduleContent(
    semestersNames: List<String>,
    selectedSemester: Semester?,
    rememberLessons: @Composable (date: LocalDate) -> State<List<Lesson>?>,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddSemesterClick: () -> Unit,
    onEditScheduleClick: (semester: Semester) -> Unit,
    onLessonClick: (Lesson) -> Unit,
) {
    var currentDate: LocalDate? by rememberSaveable { mutableStateOf(null) }

    val state = remember(selectedSemester) {
        selectedSemester?.let { SemesterWithState(it, currentDate ?: LocalDate.now()) }
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
                            onSelectedItemChange = { index, _ -> onSelectedSemesterChange(index) },
                        )
                    }
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    var showDatePicker by remember { mutableStateOf(false) }

                    TopAppBarActions(
                        actions = listOfNotNull(
                            state?.let { _ ->
                                ActionItem.AlwaysShow(
                                    name = stringResource(RS.s_calendar),
                                    imageVector = AppIcons.Today,
                                    onClick = { showDatePicker = true },
                                )
                            },
                            if (selectedSemester == null) {
                                ActionItem.AlwaysShow(
                                    name = stringResource(RS.s_add),
                                    imageVector = AppIcons.Add,
                                    onClick = onAddSemesterClick,
                                )
                            } else {
                                ActionItem.NeverShow(
                                    name = stringResource(RS.s_add),
                                    onClick = onAddSemesterClick,
                                )
                            },
                            selectedSemester?.let { semester ->
                                ActionItem.NeverShow(
                                    name = stringResource(RS.s_edit),
                                    onClick = { onEditScheduleClick(semester) },
                                )
                            },
                        ),
                    )

                    if (state != null && showDatePicker) {
                        DatePickerDialog(
                            onConfirm = { newValue ->
                                showDatePicker = false
                                coroutineScope.launch { state.animateScrollToDate(newValue) }
                            },
                            onDismiss = { showDatePicker = false },
                            initialSelectedDate = state.currentDate,
                            datesRange = state.semester.dateRange,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (state == null) {
                Text(
                    text = stringResource(RS.s_no_schedule),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.screenPaddingHorizontal),
                )
            } else {
                val shortTitleFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM") }
                val fullTitleFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy") }

                PagerTabStrip(
                    state = state.pagerState,
                    titleGetter = { page ->
                        val date = state.semester.firstDay.plusDays(page.toLong())
                        date.format(if (date.year == LocalDate.now().year) shortTitleFormatter else fullTitleFormatter)
                    },
                )

                HorizontalPager(
                    state = state.pagerState,
                    key = { state.semester.id to state.getDate(it) },
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    val lessons by rememberLessons(state.getDate(page))
                    LazyLessonsList(lessons = lessons, onLessonClick = onLessonClick)
                }
            }
        }
    }
}

@Stable
private data class SemesterWithState(val semester: Semester, val pagerState: PagerState) {

    constructor(semester: Semester, initialDate: LocalDate) : this(
        semester,
        PagerStateImpl(
            initialPage = semester.getPosition(initialDate),
            updatedPageCount = { semester.length },
        ),
    )

    companion object {
        val SemesterWithState.currentDate get() = getDate(pagerState.currentPage)
        fun SemesterWithState.getDate(page: Int) = semester.getDate(page)
        suspend fun SemesterWithState.animateScrollToDate(date: LocalDate) =
            pagerState.animateScrollToPage(semester.getPosition(date))

        private fun Semester.getDate(position: Int): LocalDate = firstDay.plusDays(position.toLong())
        private fun Semester.getPosition(date: LocalDate) =
            ChronoUnit.DAYS.between(firstDay, date.coerceIn(firstDay, lastDay)).toInt()
    }

    private class PagerStateImpl(
        initialPage: Int = 0,
        initialPageOffsetFraction: Float = 0.0f,
        updatedPageCount: () -> Int,
    ) : PagerState(initialPage, initialPageOffsetFraction) {
        var pageCountState = mutableStateOf(updatedPageCount)
        override val pageCount: Int get() = pageCountState.value.invoke()
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
        onEditScheduleClick = {},
        onLessonClick = {},
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
        onEditScheduleClick = {},
        onLessonClick = {},
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
        onEditScheduleClick = {},
        onLessonClick = {},
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
        onEditScheduleClick = {},
        onLessonClick = {},
    )
}
