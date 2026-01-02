package ru.erdenian.studentassistant.homeworks.composable

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.os.ConfigurationCompat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.DelayedVisibility
import ru.erdenian.studentassistant.uikit.utils.AppPreviews
import ru.erdenian.studentassistant.uikit.view.HomeworkCard

/**
 * Список домашних заданий.
 *
 * Отображает списки просроченных, актуальных и выполненных заданий с разделителями.
 *
 * @param overdueHomeworks список просроченных заданий.
 * @param actualHomeworks список актуальных заданий.
 * @param pastHomeworks список прошедших (выполненных) заданий.
 * @param onHomeworkClick колбэк при клике на задание.
 * @param modifier модификатор.
 * @param onLongHomeworkClick колбэк при длительном нажатии на задание.
 */
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
                    val locale = ConfigurationCompat
                        .getLocales(LocalConfiguration.current)
                        .get(0)
                        ?: Locale.getDefault()
                    val deadlineFormatter = remember(locale) {
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)
                    }

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

private data class LazyHomeworksListPreviewData(
    val overdue: List<Homework>?,
    val actual: List<Homework>?,
    val past: List<Homework>?,
)

private class LazyHomeworksListPreviewParameterProvider : PreviewParameterProvider<LazyHomeworksListPreviewData> {
    override val values = sequenceOf(
        LazyHomeworksListPreviewData(
            List(3) { Homeworks.regular },
            List(3) { Homeworks.regular },
            List(3) { Homeworks.regular },
        ),
        LazyHomeworksListPreviewData(emptyList(), emptyList(), emptyList()),
        LazyHomeworksListPreviewData(null, null, null),
    )
}

@AppPreviews
@Composable
private fun LazyHomeworksListPreview(
    @PreviewParameter(LazyHomeworksListPreviewParameterProvider::class) data: LazyHomeworksListPreviewData,
) = AppTheme {
    Surface {
        LazyHomeworksList(
            overdueHomeworks = data.overdue,
            actualHomeworks = data.actual,
            pastHomeworks = data.past,
            onHomeworkClick = {},
        )
    }
}
