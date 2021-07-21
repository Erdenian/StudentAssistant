package ru.erdenian.studentassistant.uikit.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.uikit.style.AppTheme

/**
 * Карточка домашнего задания.
 */
@Composable
fun HomeworkCard(
    subjectName: String,
    description: String,
    deadline: String,
    modifier: Modifier = Modifier
) = Card(
    modifier = modifier
) {
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.card_margin_inside))
    ) {
        Text(
            text = subjectName,
            style = MaterialTheme.typography.body1
        )

        Divider(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.divider_margin_top_bottom)))

        Text(
            text = description,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = MaterialTheme.typography.body2
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(R.string.hc_deadline, deadline),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Preview
@Composable
private fun HomeworkCardPreview() = AppTheme {
    HomeworkCard(
        subjectName = "Интернет программирование",
        description = "Сделать лабы",
        deadline = "21.08.2021"
    )
}

@Preview
@Composable
private fun HomeworkCardPreviewDark() = AppTheme(isDarkTheme = true) {
    HomeworkCard(
        subjectName = "Интернет программирование",
        description = "Сделать лабы",
        deadline = "21.08.2021"
    )
}
