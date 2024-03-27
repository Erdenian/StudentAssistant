package com.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions

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
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) = ElevatedCard(
    modifier = modifier
) {
    Column(
        modifier = Modifier
            .combinedClickable(
                enabled = (onClick != null) || (onLongClick != null),
                onLongClick = onLongClick,
                onClick = onClick ?: {}
            )
            .padding(MaterialTheme.dimensions.cardContentPadding)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = startTime,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(text = " - ")

            Text(
                text = endTime,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light)
            )

            if (classrooms.isNotEmpty()) {
                Text(
                    text = classrooms.joinToString(),
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(start = 32.dp)
                )

                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    Icon(
                        imageVector = AppIcons.LocationOn,
                        contentDescription = null
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimensions.dividerPaddingVertical))

        if (type.isNotBlank()) {
            Text(
                text = type,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
            )

            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(
            text = subjectName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge
        )

        teachers.forEach { teacher ->
            key(teacher) {
                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Icon(
                            imageVector = AppIcons.Person,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = teacher,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light)
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
private fun LessonCardMinimalPreview() = AppTheme {
    LessonCard(
        subjectName = "Интернет программирование",
        type = "",
        teachers = emptyList(),
        classrooms = emptyList(),
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
        teachers = listOf(
            "Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович",
            "Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович",
            "Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович"
        ),
        classrooms = listOf("4212а", "4212б", "4212в", "4212г", "4212д", "4212е", "4212ё", "4212ж", "4212з", "4212и", "4212й"),
        startTime = "09:00",
        endTime = "10:30"
    )
}
