package ru.erdenian.studentassistant.schedule.scheduleeditor

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.schedule.composable.LazyLessonsList
import ru.erdenian.studentassistant.schedule.composable.PagerTabStrip
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.ProgressDialog
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
    val isDeleted by viewModel.isDeleted.collectAsState()
    DisposableEffect(isDeleted) {
        if (isDeleted) navigateBack()
        onDispose {}
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val context = LocalContext.current

    val operation by viewModel.operation.collectAsState()

    val lessonsGetter = remember<(Int) -> Flow<List<Lesson>>>(viewModel) {
        { page ->
            val dayOfWeek = DayOfWeek.of(page + 1)
            viewModel.getLessons(dayOfWeek).map { it.list }
        }
    }

    var showHomeworksCounterOperation by remember { mutableStateOf(false) }
    if (showHomeworksCounterOperation) {
        ProgressDialog {
            Text(text = stringResource(R.string.le_delete_homeworks_progress))
        }
    }

    ScheduleEditorContent(
        operation = operation,
        state = pagerState,
        lessonsGetter = lessonsGetter,
        onBackClick = navigateBack,
        onEditSemesterClick = { navigateToEditSemester(viewModel.semesterId) },
        onDeleteSemesterClick = {
            MaterialAlertDialogBuilder(context)
                .setMessage(R.string.sce_delete_message)
                .setPositiveButton(R.string.sce_delete_yes) { _, _ -> viewModel.deleteSemester() }
                .setNegativeButton(R.string.sce_delete_no, null)
                .show()
        },
        onLessonClick = { navigateToEditLesson(viewModel.semesterId, it.id, false) },
        onCopyLessonClick = { navigateToEditLesson(viewModel.semesterId, it.id, true) },
        onDeleteLessonClick = { lesson ->
            showHomeworksCounterOperation = true
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
                showHomeworksCounterOperation = false
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
    operation: ScheduleEditorViewModel.Operation?,
    state: PagerState,
    lessonsGetter: (page: Int) -> Flow<List<Lesson>>,
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
    if (operation != null) {
        val stringId = when (operation) {
            ScheduleEditorViewModel.Operation.DELETING_LESSON -> R.string.sce_delete_lesson_progress
            ScheduleEditorViewModel.Operation.DELETING_SEMESTER -> R.string.sce_delete_progress
        }
        ProgressDialog { Text(text = stringResource(stringId)) }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val daysOfWeek = remember {
            DayOfWeek.values().map { it.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) }
        }

        PagerTabStrip(
            state = state,
            titleGetter = { daysOfWeek[it] }
        )

        HorizontalPager(
            count = daysOfWeek.size,
            state = state,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val lessonsFlow = remember(lessonsGetter, page) { lessonsGetter(page) }
            val lessons by lessonsFlow.collectAsState(null)
            var contextMenuLesson by remember { mutableStateOf<Lesson?>(null) }

            LazyLessonsList(
                lessons = lessons,
                onLessonClick = onLessonClick,
                onLongLessonClick = { contextMenuLesson = it }
            )

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

@OptIn(ExperimentalPagerApi::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleEditorContentPreview() = AppTheme {
    val lesson = Lessons.regular
    ScheduleEditorContent(
        operation = null,
        state = rememberPagerState(),
        lessonsGetter = { MutableStateFlow(List(10) { lesson }) },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onCopyLessonClick = {},
        onDeleteLessonClick = {},
        onAddLessonClick = {}
    )
}
