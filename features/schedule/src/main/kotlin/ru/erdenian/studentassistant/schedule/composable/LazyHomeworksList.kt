package ru.erdenian.studentassistant.schedule.composable

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
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.DelayedVisibility
import ru.erdenian.studentassistant.uikit.view.HomeworkCard

@Composable
internal fun LazyHomeworksList(
    homeworks: List<Homework>?,
    onHomeworkClick: (Homework) -> Unit,
    modifier: Modifier = Modifier,
    onLongHomeworkClick: ((Homework) -> Unit)? = null,
) {
    AnimatedContent(
        targetState = homeworks,
        contentKey = { it?.isNotEmpty() },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentAlignment = Alignment.Center,
        label = "LazyHomeworksList",
        modifier = modifier,
    ) { homeworksState ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                (homeworksState == null) -> DelayedVisibility { CircularProgressIndicator() }
                homeworksState.isEmpty() -> Text(
                    text = stringResource(RS.lhl_no_homeworks),
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
                            items = homeworksState,
                            key = { it.id },
                        ) { homework ->
                            val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
                            val haptic = LocalHapticFeedback.current

                            HomeworkCard(
                                subjectName = homework.subjectName,
                                description = homework.description,
                                deadline = homework.deadline.format(deadlineFormatter),
                                onClick = { onHomeworkClick(homework) },
                                onLongClick = onLongHomeworkClick?.let { onLongClick ->
                                    {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onLongClick(homework)
                                    }
                                },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListLoadingPreview() = AppTheme {
    LazyHomeworksList(
        homeworks = null,
        onHomeworkClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListEmptyPreview() = AppTheme {
    LazyHomeworksList(
        homeworks = emptyList(),
        onHomeworkClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListPreview() = AppTheme {
    LazyHomeworksList(
        homeworks = List(10) { Homeworks.regular },
        onHomeworkClick = {},
    )
}
