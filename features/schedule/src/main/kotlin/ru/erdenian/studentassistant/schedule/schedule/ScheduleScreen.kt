package ru.erdenian.studentassistant.schedule.schedule

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.toImmutableSortedSet
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.schedule.composable.PagerTabStrip
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.LessonCard
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu
import ru.erdenian.studentassistant.utils.showDatePicker

private fun Semester.getDate(position: Int): LocalDate = firstDay.plusDays(position)
private fun Semester.getPosition(date: LocalDate) = Days.daysBetween(firstDay, date.coerceIn(range)).days

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    navigateToAddSemester: () -> Unit,
    navigateToEditSchedule: (semesterId: Long) -> Unit,
    navigateToShowLessonInformation: (lessonId: Long) -> Unit
) {
    val semesters by viewModel.allSemesters.collectAsState()
    val semestersNames by derivedStateOf { semesters.map { it.name } }
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    ScheduleContent(
        state = pagerState,
        semestersNames = semestersNames,
        selectedSemester = selectedSemester,
        lessonsGetter = { date -> viewModel.getLessons(date) },
        onSelectedSemesterChange = { index ->
            val selectedDate = selectedSemester?.getDate(pagerState.currentPage) ?: LocalDate.now()
            val newSemester = semesters.list[index]
            viewModel.selectSemester(newSemester.id)
            coroutineScope.launch {
                pagerState.scrollToPage(newSemester.getPosition(selectedDate))
            }
        },
        onAddSemesterClick = navigateToAddSemester,
        onEditSemesterClick = { navigateToEditSchedule(checkNotNull(selectedSemester).id) },
        onLessonClick = { navigateToShowLessonInformation(it.id) }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScheduleContent(
    state: PagerState,
    semestersNames: List<String>,
    selectedSemester: Semester?,
    lessonsGetter: (date: LocalDate) -> StateFlow<ImmutableSortedSet<Lesson>>,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddSemesterClick: () -> Unit,
    onEditSemesterClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (semestersNames.size <= 1) {
                        Text(text = stringResource(R.string.s_title))
                    } else {
                        TopAppBarDropdownMenu(
                            items = semestersNames,
                            selectedItem = checkNotNull(selectedSemester).name,
                            onSelectedItemChange = { index, _ -> onSelectedSemesterChange(index) }
                        )
                    }
                },
                actions = {
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()
                    TopAppBarActions(
                        actions = listOfNotNull(
                            if (selectedSemester != null) {
                                ActionItem.AlwaysShow(
                                    name = stringResource(R.string.s_calendar),
                                    imageVector = AppIcons.Today,
                                    onClick = {
                                        context.showDatePicker(
                                            selectedSemester.getDate(state.currentPage),
                                            selectedSemester.firstDay,
                                            selectedSemester.lastDay
                                        ) {
                                            coroutineScope.launch {
                                                state.animateScrollToPage(selectedSemester.getPosition(it))
                                            }
                                        }
                                    }
                                )
                            } else null,
                            if (selectedSemester == null) {
                                ActionItem.AlwaysShow(
                                    name = stringResource(R.string.s_add),
                                    imageVector = AppIcons.Add,
                                    onClick = onAddSemesterClick
                                )
                            } else {
                                ActionItem.NeverShow(
                                    name = stringResource(R.string.s_add),
                                    onClick = onAddSemesterClick
                                )
                            },
                            if (selectedSemester != null) {
                                ActionItem.NeverShow(
                                    name = stringResource(R.string.s_edit),
                                    onClick = onEditSemesterClick
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
            if (selectedSemester == null) {
                Text(
                    text = stringResource(R.string.s_no_schedule),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
                )
            } else {
                val shortTitleFormatter = remember { DateTimeFormat.forPattern("EEEE, dd MMMM") }
                val fullTitleFormatter = remember { DateTimeFormat.forPattern("EEEE, dd MMMM yyyy") }

                PagerTabStrip(
                    state = state,
                    titleGetter = { page ->
                        val date = selectedSemester.firstDay.plusDays(page)
                        date.toString(if (date.year == LocalDate.now().year) shortTitleFormatter else fullTitleFormatter)
                    }
                )

                HorizontalPager(
                    count = selectedSemester.length,
                    state = state
                ) { page ->
                    val lessonsFlow = remember(selectedSemester, lessonsGetter) { lessonsGetter(selectedSemester.getDate(page)) }
                    val lessons by lessonsFlow.collectAsState()

                    if (lessons.isEmpty()) {
                        Text(
                            text = stringResource(R.string.s_free_day),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                                vertical = MaterialTheme.dimensions.activityVerticalMargin
                            ),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.cardsSpacing),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(
                                items = lessons.list,
                                key = { _, item -> item.id }
                            ) { _, lesson ->
                                val timeFormatter = remember { DateTimeFormat.shortTime() }

                                LessonCard(
                                    subjectName = lesson.subjectName,
                                    type = lesson.type,
                                    teachers = lesson.teachers.list,
                                    classrooms = lesson.classrooms.list,
                                    startTime = lesson.startTime.toString(timeFormatter),
                                    endTime = lesson.endTime.toString(timeFormatter),
                                    onClick = { onLessonClick(lesson) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonsEditorContentPreview() = AppTheme {
    val lesson = Lessons.regular
    ScheduleContent(
        state = rememberPagerState(),
        semestersNames = emptyList(),
        selectedSemester = Semesters.regular,
        lessonsGetter = { MutableStateFlow(List(10) { lesson }.toImmutableSortedSet()) },
        onSelectedSemesterChange = {},
        onAddSemesterClick = {},
        onEditSemesterClick = {},
        onLessonClick = {}
    )
}
