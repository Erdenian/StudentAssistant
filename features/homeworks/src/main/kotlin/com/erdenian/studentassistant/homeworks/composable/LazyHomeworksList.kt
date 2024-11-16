package com.erdenian.studentassistant.homeworks.composable

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.erdenian.studentassistant.repository.api.entity.Homework
import com.erdenian.studentassistant.sampledata.Homeworks
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.layout.DelayedVisibility
import com.erdenian.studentassistant.uikit.view.HomeworkCard
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun LazyHomeworksList(
    overdueHomeworks: List<Homework>?,
    actualHomeworks: List<Homework>?,
    pastHomeworks: List<Homework>?,
    onHomeworkClick: (Homework) -> Unit,
    modifier: Modifier = Modifier,
    onLongHomeworkClick: ((Homework) -> Unit)? = null,
) {
    AnimatedContent(
        targetState = Triple(overdueHomeworks, actualHomeworks, pastHomeworks),
        contentKey = { (overdue, actual, past) ->
            when {
                (overdue == null) || (actual == null) || (past == null) -> null
                overdue.isEmpty() && actual.isEmpty() && past.isEmpty() -> false
                else -> true
            }
        },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentAlignment = Alignment.Center,
        label = "LazyHomeworksList",
        modifier = modifier,
    ) { (overdue, actual, past) ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                (overdue == null) || (actual == null) || (past == null) -> {
                    DelayedVisibility { CircularProgressIndicator() }
                }
                overdue.isEmpty() && actual.isEmpty() && past.isEmpty() -> {
                    Text(
                        text = stringResource(RS.lhl_no_homeworks),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.screenPaddingHorizontal),
                    )
                }
                else -> {
                    val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }

                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                            vertical = MaterialTheme.dimensions.screenPaddingVertical,
                        ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.cardsSpacing),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        fun LazyListScope.createList(homeworks: List<Homework>) = items(
                            items = homeworks,
                            key = { it.id },
                        ) { homework ->
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
                                onClick = { onHomeworkClick(homework) },
                                modifier = Modifier.animateItem(),
                            )
                        }

                        createList(overdue)
                        if (overdue.isNotEmpty() && (actual.isNotEmpty() || past.isNotEmpty())) {
                            item { HorizontalDivider(modifier = Modifier.animateItem()) }
                        }
                        createList(actual)
                        if (actual.isNotEmpty() && past.isNotEmpty()) {
                            item { HorizontalDivider(modifier = Modifier.animateItem()) }
                        }
                        createList(past)
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
        onHomeworkClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListEmptyPreview() = AppTheme {
    LazyHomeworksList(
        overdueHomeworks = emptyList(),
        actualHomeworks = emptyList(),
        pastHomeworks = emptyList(),
        onHomeworkClick = {},
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LazyHomeworksListPreview() = AppTheme {
    LazyHomeworksList(
        overdueHomeworks = List(4) { Homeworks.regular },
        actualHomeworks = List(4) { Homeworks.regular },
        pastHomeworks = List(4) { Homeworks.regular },
        onHomeworkClick = {},
    )
}
