package ru.erdenian.studentassistant.schedule.scheduleeditor

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.toImmutableSortedSet
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.schedule.composable.PagerTabStrip
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.LessonCard
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScheduleEditorScreen(
    viewModel: ScheduleEditorViewModel,
    navigateBack: () -> Unit,
    navigateToEditSemester: (semesterId: Long) -> Unit,
    navigateToEditLesson: (semesterId: Long, lessonId: Long, copy: Boolean) -> Unit,
    navigateToCreateLesson: (semesterId: Long, dayOfWeek: DayOfWeek) -> Unit
) {
    val semester by viewModel.semester.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    DisposableEffect(isDeleted) {
        if (isDeleted) navigateBack()
        onDispose {}
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val context = LocalContext.current

    ScheduleEditorContent(
        state = pagerState,
        lessonsGetter = { page ->
            val dayOfWeek = DayOfWeek.of(page + 1)
            viewModel.getLessons(dayOfWeek)
        },
        onBackClick = navigateBack,
        onEditSemesterClick = { navigateToEditSemester(viewModel.semesterId) },
        onDeleteSemesterClick = {
            MaterialAlertDialogBuilder(context)
                .setMessage(R.string.sce_delete_message)
                .setPositiveButton(R.string.sce_delete_yes) { _, _ -> viewModel.deleteSemester() }
                .setNegativeButton(R.string.sce_delete_no, null)
                .show()
        },
        onLessonClick = { navigateToEditLesson(checkNotNull(semester).id, it.id, false) },
        onCopyLessonClick = { navigateToEditLesson(checkNotNull(semester).id, it.id, true) },
        onDeleteLessonClick = { lesson ->
            coroutineScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson)) {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.le_delete_homeworks_title)
                        .setMessage(R.string.le_delete_homeworks_message)
                        .setPositiveButton(R.string.le_delete_homeworks_yes) { _, _ ->
                            viewModel.deleteLesson(lesson, true)
                        }
                        .setNegativeButton(R.string.le_delete_homeworks_no) { _, _ ->
                            viewModel.deleteLesson(lesson, false)
                        }
                        .setNeutralButton(R.string.le_delete_homeworks_cancel, null)
                        .show()
                } else {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.le_delete_message)
                        .setPositiveButton(R.string.le_delete_yes) { _, _ ->
                            viewModel.viewModelScope.launch { viewModel.deleteLesson(lesson) }
                        }
                        .setNegativeButton(R.string.le_delete_no, null)
                        .show()
                }
            }
        },
        onAddLessonClick = {
            val dayOfWeek = DayOfWeek.of(pagerState.currentPage + 1)
            navigateToCreateLesson(viewModel.semesterId, dayOfWeek)
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScheduleEditorContent(
    state: PagerState,
    lessonsGetter: (page: Int) -> StateFlow<ImmutableSortedSet<Lesson>>,
    onBackClick: () -> Unit,
    onEditSemesterClick: () -> Unit,
    onDeleteSemesterClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onCopyLessonClick: (Lesson) -> Unit,
    onDeleteLessonClick: (Lesson) -> Unit,
    onAddLessonClick: () -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.sce_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOf(
                        ActionItem.NeverShow(
                            name = stringResource(R.string.sce_edit),
                            onClick = onEditSemesterClick
                        ),
                        ActionItem.NeverShow(
                            name = stringResource(R.string.sce_delete),
                            onClick = onDeleteSemesterClick
                        )
                    )
                )
            }
        )
    },
    floatingActionButton = {
        FloatingActionButton(onClick = onAddLessonClick) {
            Icon(imageVector = AppIcons.Add, contentDescription = null)
        }
    }
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val daysOfWeek = DayOfWeek.values().map { it.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) }

        PagerTabStrip(
            state = state,
            titleGetter = { daysOfWeek[it] }
        )

        HorizontalPager(
            count = daysOfWeek.size,
            state = state
        ) { page ->
            val lessonsFlow = remember(lessonsGetter) { lessonsGetter(page) }
            val lessons by lessonsFlow.collectAsState()

            if (lessons.isEmpty()) {
                Text(
                    text = stringResource(R.string.sce_free_day),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
                )
            } else {
                var contextMenuLesson by remember { mutableStateOf<Lesson?>(null) }

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
                        val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }

                        LessonCard(
                            subjectName = lesson.subjectName,
                            type = lesson.type,
                            teachers = lesson.teachers.list,
                            classrooms = lesson.classrooms.list,
                            startTime = lesson.startTime.format(timeFormatter),
                            endTime = lesson.endTime.format(timeFormatter),
                            onClick = { onLessonClick(lesson) },
                            onLongClick = { contextMenuLesson = lesson }
                        )
                    }
                }

                DropdownMenu(
                    expanded = (contextMenuLesson != null),
                    onDismissRequest = { contextMenuLesson = null }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            val lesson = checkNotNull(contextMenuLesson)
                            contextMenuLesson = null
                            onCopyLessonClick(lesson)
                        }
                    ) {
                        Text(text = stringResource(R.string.sce_copy_lesson))
                    }
                    DropdownMenuItem(
                        onClick = {
                            val lesson = checkNotNull(contextMenuLesson)
                            contextMenuLesson = null
                            onDeleteLessonClick(lesson)
                        }
                    ) {
                        Text(text = stringResource(R.string.sce_delete_lesson))
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
private fun ScheduleEditorContentPreview() = AppTheme {
    val lesson = Lessons.regular
    ScheduleEditorContent(
        state = rememberPagerState(),
        lessonsGetter = { MutableStateFlow(List(10) { lesson }.toImmutableSortedSet()) },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onCopyLessonClick = {},
        onDeleteLessonClick = {},
        onAddLessonClick = {}
    )
}
