package ru.erdenian.studentassistant.uikit.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.uikit.style.AppTheme

/**
 * Карточка пары.
 */
@Composable
fun LessonCard(
    subjectName: String,
    type: String,
    teachers: List<String>,
    classrooms: List<String>,
    startTime: String,
    endTime: String,
    modifier: Modifier = Modifier
) = Card(
    elevation = 4.dp,
    modifier = modifier
) {
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.card_margin_inside))
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = startTime,
                style = MaterialTheme.typography.body1
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = " - ")
                Text(
                    text = endTime,
                    style = MaterialTheme.typography.body1
                )

                Spacer(modifier = Modifier.weight(1.0f))

                if (classrooms.isNotEmpty()) {
                    Text(
                        text = classrooms.joinToString(),
                        style = MaterialTheme.typography.body1
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_map_marker),
                        contentDescription = null,
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.divider_margin_top_bottom)))

        if (type.isNotBlank()) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Text(
            text = subjectName,
            style = MaterialTheme.typography.body1
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            teachers.forEach { teacher ->
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = modifier
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_account),
                        contentDescription = null,
                    )

                    Text(
                        text = teacher,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LessonCardPreview() = AppTheme {
    LessonCard(
        subjectName = "Интернет программирование",
        type = "Лабораторная работа",
        teachers = listOf("Кожухов Игорь Борисович"),
        classrooms = listOf("4212а", "4212б"),
        startTime = "09:00",
        endTime = "10:30"
    )
}

@Preview
@Composable
private fun LessonCardPreviewDark() = AppTheme(isDarkTheme = true) {
    LessonCard(
        subjectName = "Интернет программирование",
        type = "Лабораторная работа",
        teachers = listOf("Кожухов Игорь Борисович"),
        classrooms = listOf("4212а", "4212б"),
        startTime = "09:00",
        endTime = "10:30"
    )
}
