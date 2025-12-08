package ru.erdenian.studentassistant.schedule.scheduleeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.os.ConfigurationCompat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.composable.LazyLessonsList
import ru.erdenian.studentassistant.schedule.composable.PagerTabStrip
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.AutoMirrored
import ru.erdenian.studentassistant.uikit.layout.ContextMenuBox
import ru.erdenian.studentassistant.uikit.utils.ScreenPreviews
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

@Composable
internal fun ScheduleEditorContent(
    rememberLessons: @Composable (page: Int) -> State<List<Lesson>?>,
    onBackClick: () -> Unit,
    onEditSemesterClick: () -> Unit,
    onDeleteSemesterClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onCopyLessonClick: (Lesson) -> Unit,
    onDeleteLessonClick: (Lesson) -> Unit,
    onAddLessonClick: (DayOfWeek) -> Unit,
) {
    val daysOfWeekTitles = run {
        val configuration = LocalConfiguration.current
        remember(configuration) {
            val locale = ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
            // TextStyle.FULL_STANDALONE возвращает число
            // https://stackoverflow.com/questions/63415047
            DayOfWeek.entries.map { it.getDisplayName(TextStyle.FULL, locale) }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { daysOfWeekTitles.size },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(RS.sce_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TopAppBarActions(
                        actions = listOf(
                            ActionItem.NeverShow(
                                name = stringResource(RS.sce_edit),
                                onClick = onEditSemesterClick,
                            ),
                            ActionItem.NeverShow(
                                name = stringResource(RS.sce_delete),
                                onClick = onDeleteSemesterClick,
                            ),
                        ),
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddLessonClick(DayOfWeek.of(pagerState.currentPage + 1)) }) {
                Icon(imageVector = AppIcons.Add, contentDescription = null)
            }
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PagerTabStrip(
                state = pagerState,
                titleGetter = { daysOfWeekTitles[it] },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val lessons by rememberLessons(page)
                var contextMenuLesson by remember { mutableStateOf<Lesson?>(null) }

                ContextMenuBox(
                    expanded = (contextMenuLesson != null),
                    onDismissRequest = { contextMenuLesson = null },
                    contextMenu = {
                        DropdownMenuItem(
                            text = { Text(stringResource(RS.sce_copy_lesson)) },
                            onClick = {
                                val homework = checkNotNull(contextMenuLesson)
                                contextMenuLesson = null
                                onCopyLessonClick(homework)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(RS.sce_delete_lesson)) },
                            onClick = {
                                val homework = checkNotNull(contextMenuLesson)
                                contextMenuLesson = null
                                onDeleteLessonClick(homework)
                            },
                        )
                    },
                ) {
                    LazyLessonsList(
                        lessons = lessons,
                        onLessonClick = onLessonClick,
                        onLongLessonClick = { contextMenuLesson = it },
                    )
                }
            }
        }
    }
}

private data class ScheduleEditorContentPreviewData(
    val lessons: List<Lesson>?,
    val isLoading: Boolean = false,
)

@Suppress("MagicNumber")
private class ScheduleEditorContentPreviewParameterProvider :
    PreviewParameterProvider<ScheduleEditorContentPreviewData> {
    override val values = sequenceOf(
        ScheduleEditorContentPreviewData(lessons = null, isLoading = true),
        ScheduleEditorContentPreviewData(lessons = emptyList()),
        ScheduleEditorContentPreviewData(lessons = List(10) { Lessons.regular }),
    )
}

@ScreenPreviews
@Composable
private fun ScheduleEditorContentPreview(
    @PreviewParameter(ScheduleEditorContentPreviewParameterProvider::class) data: ScheduleEditorContentPreviewData,
) = AppTheme {
    val state = remember { mutableStateOf(data.lessons) }
    ScheduleEditorContent(
        rememberLessons = { state },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onCopyLessonClick = {},
        onDeleteLessonClick = {},
        onAddLessonClick = {},
    )
}
