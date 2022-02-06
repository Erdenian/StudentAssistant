package ru.erdenian.studentassistant.schedule.composable

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.DelayedVisibility
import ru.erdenian.studentassistant.uikit.view.LessonCard

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun LazyLessonsList(
    lessons: List<Lesson>?,
    onLessonClick: (Lesson) -> Unit,
    modifier: Modifier = Modifier,
    onLongLessonClick: ((Lesson) -> Unit)? = null
) {
    AnimatedContent(
        targetState = lessons,
        transitionSpec = { fadeIn() with fadeOut() },
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) { lessonsState ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                (lessonsState == null) -> DelayedVisibility { CircularProgressIndicator() }
                lessonsState.isEmpty() -> Text(
                    text = stringResource(R.string.lll_free_day),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
                )
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                            vertical = MaterialTheme.dimensions.activityVerticalMargin
                        ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.cardsSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            items = lessonsState,
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
                                onLongClick = onLongLessonClick?.let { { it(lesson) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LazyLessonsListLoadingPreview() = AppTheme {
    LazyLessonsList(
        lessons = null,
        onLessonClick = {}
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LazyLessonsListEmptyPreview() = AppTheme {
    LazyLessonsList(
        lessons = emptyList(),
        onLessonClick = {}
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LazyLessonsListPreview() = AppTheme {
    LazyLessonsList(
        lessons = List(10) { Lessons.regular },
        onLessonClick = {}
    )
}
