package ru.erdenian.studentassistant.uikit.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.erdenian.studentassistant.uikit.R

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

        Text(
            text = stringResource(R.string.hc_deadline, deadline),
            color = colorResource(R.color.secondary_text),
            style = MaterialTheme.typography.body2
        )
    }
}
