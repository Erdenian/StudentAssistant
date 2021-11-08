package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme

/**
 * Карточка пары.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LessonCard(
    subjectName: String,
    type: String,
    teachers: List<String>,
    classrooms: List<String>,
    startTime: String,
    endTime: String,
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
            .padding(AppTheme.dimensions.cardMarginInside)
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

                if (classrooms.isNotEmpty()) {
                    TextCompat(
                        text = classrooms.joinToString(),
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .weight(1.0f)
                            .padding(start = 32.dp)
                    )
                    Icon(
                        imageVector = AppIcons.LocationOn,
                        contentDescription = null
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = AppTheme.dimensions.dividerMarginTopBottom))

        if (type.isNotBlank()) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = type,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Text(
            text = subjectName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body1
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            teachers.forEach { teacher ->
                key(teacher) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Icon(
                            imageVector = AppIcons.Person,
                            contentDescription = null
                        )

                        Text(
                            text = teacher,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "LessonCard preview")
@Preview(name = "LessonCard preview (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
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
private fun LessonCardLongPreview() = AppTheme {
    LessonCard(
        subjectName = "Интернет программирование программирование программирование программирование программирование",
        type = "Лабораторная работа работа работа работа работа работа работа работа работа работа работа работа работа работа",
        teachers = listOf("Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович"),
        classrooms = listOf("4212а", "4212б", "4212в", "4212г", "4212д", "4212е", "4212ё", "4212ж", "4212з", "4212и", "4212й"),
        startTime = "09:00",
        endTime = "10:30"
    )
}
