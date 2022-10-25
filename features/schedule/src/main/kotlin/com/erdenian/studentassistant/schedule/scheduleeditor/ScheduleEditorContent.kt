package com.erdenian.studentassistant.schedule.scheduleeditor

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.erdenian.studentassistant.style.AutoMirrored
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun ScheduleEditorContent(
    rememberLessons: @Composable (page: Int) -> State<List<Lesson>?>,
    onBackClick: () -> Unit,
    onEditSemesterClick: () -> Unit,
    onDeleteSemesterClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onLongLessonClick: (Lesson) -> Unit,
    onAddLessonClick: (DayOfWeek) -> Unit
) {
    val daysOfWeekTitles = remember {
        // TextStyle.FULL_STANDALONE returns number
        // https://stackoverflow.com/questions/63415047
        DayOfWeek.entries.map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { daysOfWeekTitles.size }
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
            FloatingActionButton(onClick = { onAddLessonClick(DayOfWeek.of(pagerState.currentPage + 1)) }) {
                Icon(imageVector = AppIcons.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PagerTabStrip(
                state = pagerState,
                titleGetter = { daysOfWeekTitles[it] }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val lessons by rememberLessons(page)

                LazyLessonsList(
                    lessons = lessons,
                    onLessonClick = onLessonClick,
                    onLongLessonClick = onLongLessonClick
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
        rememberLessons = { remember { mutableStateOf(null) } },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onLongLessonClick = {},
        onAddLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleEditorContentNoLessonsPreview() = AppTheme {
    ScheduleEditorContent(
        rememberLessons = { remember { mutableStateOf(emptyList()) } },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onLongLessonClick = {},
        onAddLessonClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleEditorContentPreview() = AppTheme {
    val lessons = List(10) { Lessons.regular }
    ScheduleEditorContent(
        rememberLessons = { remember { mutableStateOf(lessons) } },
        onBackClick = {},
        onEditSemesterClick = {},
        onDeleteSemesterClick = {},
        onLessonClick = {},
        onLongLessonClick = {},
        onAddLessonClick = {}
    )
}
