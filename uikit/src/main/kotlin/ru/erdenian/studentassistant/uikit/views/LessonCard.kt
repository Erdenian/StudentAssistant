package ru.erdenian.studentassistant.uikit.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.erdenian.studentassistant.uikit.R

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
            Text(
                text = " - ",
                color = colorResource(R.color.secondary_text)
            )
            Text(
                text = endTime,
                color = colorResource(R.color.secondary_text),
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.weight(1.0f))

            if (classrooms.isNotEmpty()) {
                Text(
                    text = classrooms.joinToString(),
                    color = colorResource(R.color.secondary_text),
                    style = MaterialTheme.typography.body1
                )
                Icon(
                    painter = painterResource(R.drawable.ic_map_marker),
                    contentDescription = null,
                    tint = colorResource(R.color.secondary_text)
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.divider_margin_top_bottom)))

        if (type.isNotBlank()) Text(
            text = type,
            color = colorResource(R.color.secondary_text),
            style = MaterialTheme.typography.body2
        )
        Text(
            text = subjectName,
            style = MaterialTheme.typography.body1
        )

        teachers.forEach { teacher ->
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = modifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_account),
                    contentDescription = null,
                    tint = colorResource(R.color.secondary_text)
                )

                Text(
                    text = teacher,
                    color = colorResource(R.color.secondary_text),
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Preview
@Composable
private fun LessonCardPreview() = LessonCard(
    subjectName = "Интернет программирование",
    type = "Лабораторная работа",
    teachers = listOf("Кожухов Игорь Борисович"),
    classrooms = listOf("4212а", "4212б"),
    startTime = "09:00",
    endTime = "10:30"
)
