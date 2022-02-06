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
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.DelayedVisibility
import ru.erdenian.studentassistant.uikit.view.HomeworkCard

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun LazyHomeworksList(
    homeworks: List<Homework>?,
    onHomeworkClick: (Homework) -> Unit,
    modifier: Modifier = Modifier,
    onLongHomeworkClick: ((Homework) -> Unit)? = null
) {
    AnimatedContent(
        targetState = homeworks,
        transitionSpec = { fadeIn() with fadeOut() },
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) { homeworksState ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                (homeworksState == null) -> DelayedVisibility { CircularProgressIndicator() }
                homeworksState.isEmpty() -> Text(
                    text = stringResource(R.string.lhl_no_homeworks),
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
                            items = homeworksState,
                            key = { _, item -> item.id }
                        ) { _, homework ->
                            val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }

                            HomeworkCard(
                                subjectName = homework.subjectName,
                                description = homework.description,
                                deadline = homework.deadline.format(deadlineFormatter),
                                onClick = { onHomeworkClick(homework) },
                                onLongClick = onLongHomeworkClick?.let { { it(homework) } }
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
private fun LazyHomeworksListLoadingPreview() = AppTheme {
    LazyHomeworksList(
        homeworks = null,
        onHomeworkClick = {}
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LazyHomeworksListEmptyPreview() = AppTheme {
    LazyHomeworksList(
        homeworks = emptyList(),
        onHomeworkClick = {}
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LazyHomeworksListPreview() = AppTheme {
    LazyHomeworksList(
        homeworks = List(10) { Homeworks.regular },
        onHomeworkClick = {}
    )
}
