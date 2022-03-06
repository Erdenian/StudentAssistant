package com.erdenian.studentassistant.schedule.scheduleeditor

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.sampledata.Lessons
import com.erdenian.studentassistant.schedule.composable.LazyLessonsList
import com.erdenian.studentassistant.schedule.composable.PagerTabStrip
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.ContextMenuDialog
import com.erdenian.studentassistant.uikit.view.ContextMenuItem
import com.erdenian.studentassistant.uikit.view.ProgressDialog
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun ScheduleEditorScreen(
    viewModel: ScheduleEditorViewModel,
    navigateBack: () -> Unit,
    navigateToEditSemester: (semesterId: Long) -> Unit,
    navigateToEditLesson: (semesterId: Long, lessonId: Long, copy: Boolean) -> Unit,
    navigateToCreateLesson: (semesterId: Long, dayOfWeek: DayOfWeek) -> Unit
) {
    val isDeleted by viewModel.isDeleted.collectAsState()
    LaunchedEffect(isDeleted) {
        if (isDeleted) navigateBack()
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val operation by viewModel.operation.collectAsState()

    val rememberLessons = remember<@Composable (Int) -> State<List<Lesson>?>>(viewModel) {
        { page ->
            produceState<List<Lesson>?>(null, page) {
                val dayOfWeek = DayOfWeek.of(page + 1)
                viewModel.getLessons(dayOfWeek).map { it.list }.collect { value = it }
            }
        }
    }

    var showHomeworksCounterOperation by remember { mutableStateOf(false) }
    if (showHomeworksCounterOperation) {
        ProgressDialog(stringResource(RS.le_delete_homeworks_progress))
    }

    var showDeleteSemesterDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteSemesterDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSemesterDialog = false },
            text = { Text(text = stringResource(RS.sce_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteSemesterDialog = false },
                    content = { Text(text = stringResource(RS.sce_delete_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSemester()
                        showDeleteSemesterDialog = false
                    },
                    content = { Text(text = stringResource(RS.sce_delete_yes)) }
                )
            }
        )
    }

    var lessonForDeleteWithHomeworksDialog: Lesson? by rememberSaveable { mutableStateOf(null) }
    lessonForDeleteWithHomeworksDialog?.let { lesson ->
        AlertDialog(
            onDismissRequest = { lessonForDeleteWithHomeworksDialog = null },
            title = { Text(text = stringResource(RS.le_delete_homeworks_title)) },
            text = { Text(text = stringResource(RS.le_delete_homeworks_message)) },
            dismissButton = {
                TextButton(
                    onClick = { lessonForDeleteWithHomeworksDialog = null },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_cancel)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLesson(lesson, false)
                        lessonForDeleteWithHomeworksDialog = null
                    },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_no)) }
                )
                TextButton(
                    onClick = {
                        viewModel.deleteLesson(lesson, true)
                        lessonForDeleteWithHomeworksDialog = null
                    },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_yes)) }
                )
            }
        )
    }

    var lessonForDeleteWithoutHomeworksDialog: Lesson? by rememberSaveable { mutableStateOf(null) }
    lessonForDeleteWithoutHomeworksDialog?.let { lesson ->
        AlertDialog(
            onDismissRequest = { lessonForDeleteWithoutHomeworksDialog = null },
            text = { Text(text = stringResource(RS.le_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { lessonForDeleteWithoutHomeworksDialog = null },
                    content = { Text(text = stringResource(RS.le_delete_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLesson(lesson)
                        lessonForDeleteWithoutHomeworksDialog = null
                    },
                    content = { Text(text = stringResource(RS.le_delete_yes)) }
                )
            }
        )
    }

    ScheduleEditorContent(
        operation = operation,
        state = pagerState,
        rememberLessons = rememberLessons,
        onBackClick = navigateBack,
        onEditSemesterClick = { navigateToEditSemester(viewModel.semesterId) },
        onDeleteSemesterClick = { showDeleteSemesterDialog = true },
        onLessonClick = { navigateToEditLesson(viewModel.semesterId, it.id, false) },
        onCopyLessonClick = { navigateToEditLesson(viewModel.semesterId, it.id, true) },
        onDeleteLessonClick = { lesson ->
            showHomeworksCounterOperation = true
            coroutineScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson)) lessonForDeleteWithHomeworksDialog = lesson
                else lessonForDeleteWithoutHomeworksDialog = lesson
                showHomeworksCounterOperation = false
            }
        },
        onAddLessonClick = {
            val dayOfWeek = DayOfWeek.of(pagerState.currentPage + 1)
            navigateToCreateLesson(viewModel.semesterId, dayOfWeek)
        }
    )
}

@Composable
private fun ScheduleEditorContent(
    operation: ScheduleEditorViewModel.Operation?,
    state: PagerState,
    rememberLessons: @Composable (page: Int) -> State<List<Lesson>?>,
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
            title = { Text(text = stringResource(RS.sce_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOf(
                        ActionItem.NeverShow(
                            name = stringResource(RS.sce_edit),
                            onClick = onEditSemesterClick
                        ),
                        ActionItem.NeverShow(
                            name = stringResource(RS.sce_delete),
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
    if (operation != null) {
        val stringId = when (operation) {
            ScheduleEditorViewModel.Operation.DELETING_LESSON -> RS.sce_delete_lesson_progress
            ScheduleEditorViewModel.Operation.DELETING_SEMESTER -> RS.sce_delete_progress
        }
        ProgressDialog(stringResource(stringId))
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val daysOfWeek = remember {
            // TextStyle.FULL_STANDALONE returns number
            // https://stackoverflow.com/questions/63415047
            DayOfWeek.values().map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
        }
        val pageCount = daysOfWeek.size

        PagerTabStrip(
            count = pageCount,
            state = state,
            titleGetter = { daysOfWeek[it] }
        )

        HorizontalPager(
            count = pageCount,
            state = state,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val lessons by rememberLessons(page)
            var contextMenuLesson by remember { mutableStateOf<Lesson?>(null) }

            LazyLessonsList(
                lessons = lessons,
                onLessonClick = onLessonClick,
                onLongLessonClick = { contextMenuLesson = it }
            )

            contextMenuLesson?.let { lesson ->
                ContextMenuDialog(
                    onDismissRequest = { contextMenuLesson = null },
                    title = lesson.subjectName,
                    items = listOf(
                        ContextMenuItem(stringResource(RS.sce_copy_lesson)) {
                            contextMenuLesson = null
                            onCopyLessonClick(lesson)
                        },
                        ContextMenuItem(stringResource(RS.sce_delete_lesson)) {
                            contextMenuLesson = null
                            onDeleteLessonClick(lesson)
                        }
                    )
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleEditorContentLoadingPreview() = AppTheme {
    ScheduleEditorContent(
        operation = null,
        state = rememberPagerState(),
        rememberLessons = { remember { mutableStateOf(null) } },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onCopyLessonClick = {},
        onDeleteLessonClick = {},
        onAddLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleEditorContentNoLessonsPreview() = AppTheme {
    ScheduleEditorContent(
        operation = null,
        state = rememberPagerState(),
        rememberLessons = { remember { mutableStateOf(emptyList()) } },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onCopyLessonClick = {},
        onDeleteLessonClick = {},
        onAddLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleEditorContentPreview() = AppTheme {
    val lessons = List(10) { Lessons.regular }
    ScheduleEditorContent(
        operation = null,
        state = rememberPagerState(),
        rememberLessons = { remember { mutableStateOf(lessons) } },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onCopyLessonClick = {},
        onDeleteLessonClick = {},
        onAddLessonClick = {}
    )
}
