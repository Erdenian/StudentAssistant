package ru.erdenian.studentassistant.homeworks.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.DelayedVisibility
import ru.erdenian.studentassistant.uikit.view.HomeworkCard

@Composable
internal fun LazyHomeworksList(
    overdueHomeworks: List<Homework>?,
    actualHomeworks: List<Homework>?,
    pastHomeworks: List<Homework>?,
    onHomeworkClick: (Homework) -> Unit,
    modifier: Modifier = Modifier,
    onLongHomeworkClick: ((Homework) -> Unit)? = null
) {
    AnimatedContent(
        targetState = Triple(overdueHomeworks, actualHomeworks, pastHomeworks),
        transitionSpec = { fadeIn() with fadeOut() },
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) { (overdueHomeworksState, actualHomeworksState, pastHomeworksState) ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                (overdueHomeworksState == null) || (actualHomeworksState == null) || (pastHomeworksState == null) ->
                    DelayedVisibility { CircularProgressIndicator() }
                overdueHomeworksState.isEmpty() && actualHomeworksState.isEmpty() && pastHomeworksState.isEmpty() -> Text(
                    text = stringResource(RS.lhl_no_homeworks),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
                )
                else -> {
                    val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }

                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                            vertical = MaterialTheme.dimensions.activityVerticalMargin
                        ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.cardsSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        fun LazyListScope.createList(homeworks: List<Homework>) = itemsIndexed(
                            items = homeworks,
                            key = { _, item -> item.id }
                        ) { _, homework ->
                            val haptic = LocalHapticFeedback.current
                            HomeworkCard(
                                subjectName = homework.subjectName,
                                description = homework.description,
                                deadline = homework.deadline.format(deadlineFormatter),
                                onLongClick = onLongHomeworkClick?.let { onLongClick ->
                                    {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onLongClick(homework)
                                    }
                                },
                                onClick = { onHomeworkClick(homework) }
                            )
                        }

                        createList(overdueHomeworksState)
                        if (
                            overdueHomeworksState.isNotEmpty() &&
                            (actualHomeworksState.isNotEmpty() || pastHomeworksState.isNotEmpty())
                        ) {
                            item { Divider() }
                        }
                        createList(actualHomeworksState)
                        if (actualHomeworksState.isNotEmpty() && pastHomeworksState.isNotEmpty()) {
                            item { Divider() }
                        }
                        createList(pastHomeworksState)
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
        overdueHomeworks = null,
        actualHomeworks = null,
        pastHomeworks = null,
        onHomeworkClick = {}
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListEmptyPreview() = AppTheme {
    LazyHomeworksList(
        overdueHomeworks = emptyList(),
        actualHomeworks = emptyList(),
        pastHomeworks = emptyList(),
        onHomeworkClick = {}
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListPreview() = AppTheme {
    LazyHomeworksList(
        overdueHomeworks = List(4) { Homeworks.regular },
        actualHomeworks = List(4) { Homeworks.regular },
        pastHomeworks = List(4) { Homeworks.regular },
        onHomeworkClick = {}
    )
}
