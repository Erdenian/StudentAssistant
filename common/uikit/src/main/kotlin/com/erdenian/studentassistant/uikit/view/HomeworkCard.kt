package com.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions

/**
 * Карточка домашнего задания.
 */
@Composable
fun HomeworkCard(
    subjectName: String,
    description: String,
    deadline: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) = Card(
    elevation = 4.dp,
    modifier = modifier
) {
    Column(
        modifier = Modifier
            .combinedClickable(
                enabled = (onClick != null) || (onLongClick != null),
                onLongClick = onLongClick,
                onClick = onClick ?: {}
            )
            .padding(MaterialTheme.dimensions.cardMarginInside)
    ) {
        Text(
            text = subjectName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body1
        )

        Divider(modifier = Modifier.padding(vertical = MaterialTheme.dimensions.dividerMarginTopBottom))

        Text(
            text = description,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.fillMaxWidth()
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(RS.hc_deadline, deadline),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Preview(name = "HomeworkCard preview")
@Preview(name = "HomeworkCard preview (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworkCardPreview() = AppTheme {
    HomeworkCard(
        subjectName = "Интернет программирование",
        description = "Лабораторная работа",
        deadline = "21.08.2021"
    )
}

@Preview
@Composable
private fun HomeworkCardLongPreview() = AppTheme {
    HomeworkCard(
        subjectName = "Интернет программирование программирование программирование программирование программирование",
        description = "Лабораторная работа" + " работа".repeat(100),
        deadline = "21.08.2021"
    )
}

@Preview
@Composable
private fun HomeworkCardMultilinePreview() = AppTheme {
    HomeworkCard(
        subjectName = "Интернет программирование программирование программирование программирование программирование",
        description = "Лабораторная работа" + "\nЛабораторная работа".repeat(100),
        deadline = "21.08.2021"
    )
}
