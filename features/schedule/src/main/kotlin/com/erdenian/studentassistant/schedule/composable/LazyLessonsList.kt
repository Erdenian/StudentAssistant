package com.erdenian.studentassistant.schedule.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.navigation.LocalAnimatedContentScope
import com.erdenian.studentassistant.navigation.LocalSharedTransitionScope
import com.erdenian.studentassistant.repository.api.entity.Lesson
import com.erdenian.studentassistant.sampledata.Lessons
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.layout.DelayedVisibility
import com.erdenian.studentassistant.uikit.view.LessonCard
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun LazyLessonsList(
    lessons: List<Lesson>?,
    onLessonClick: (Lesson) -> Unit,
    modifier: Modifier = Modifier,
    onLongLessonClick: ((Lesson) -> Unit)? = null,
) {
    AnimatedContent(
        targetState = lessons,
        contentKey = { it?.isNotEmpty() },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentAlignment = Alignment.Center,
        label = "LazyLessonsList",
        modifier = modifier,
    ) { lessonsState ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                (lessonsState == null) -> DelayedVisibility { CircularProgressIndicator() }
                lessonsState.isEmpty() -> Text(
                    text = stringResource(RS.lll_free_day),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.screenPaddingHorizontal),
                )
                else ->
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                            vertical = MaterialTheme.dimensions.screenPaddingVertical,
                        ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.cardsSpacing),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            items = lessonsState,
                            key = { it.id },
                        ) { lesson ->
                            val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
                            val haptic = LocalHapticFeedback.current

                            with(LocalSharedTransitionScope.current) {
                                LessonCard(
                                    subjectName = lesson.subjectName,
                                    type = lesson.type,
                                    teachers = lesson.teachers,
                                    classrooms = lesson.classrooms,
                                    startTime = lesson.startTime.format(timeFormatter),
                                    endTime = lesson.endTime.format(timeFormatter),
                                    onClick = { onLessonClick(lesson) },
                                    onLongClick = onLongLessonClick?.let { onLongClick ->
                                        {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onLongClick(lesson)
                                        }
                                    },
                                    modifier = Modifier
                                        .animateItem()
                                        .sharedElement(
                                            rememberSharedContentState(lesson),
                                            LocalAnimatedContentScope.current,
                                        ),
                                )
                            }
                        }
                    }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LazyLessonsListLoadingPreview() = AppTheme {
    LazyLessonsList(
        lessons = null,
        onLessonClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyLessonsListEmptyPreview() = AppTheme {
    LazyLessonsList(
        lessons = emptyList(),
        onLessonClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyLessonsListPreview() = AppTheme {
    LazyLessonsList(
        lessons = List(10) { Lessons.regular },
        onLessonClick = {},
    )
}
