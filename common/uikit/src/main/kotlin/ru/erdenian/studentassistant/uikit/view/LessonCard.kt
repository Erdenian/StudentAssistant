package ru.erdenian.studentassistant.uikit.view

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.utils.AppPreviews

/**
 * Карточка, отображающая краткую информацию о занятии.
 *
 * Используется в списках занятий. Отображает время, аудитории, тип занятия, название предмета и преподавателей.
 *
 * @param subjectName название предмета.
 * @param type тип занятия (например, "Лекция", "Лабораторная работа").
 * @param teachers список имен преподавателей.
 * @param classrooms список номеров аудиторий.
 * @param startTime время начала занятия (уже отформатированное).
 * @param endTime время окончания занятия (уже отформатированное).
 * @param modifier модификатор для настройки внешнего вида карточки.
 * @param onClick действие при клике на карточку.
 * @param onLongClick действие при длительном нажатии на карточку.
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
    onLongClick: (() -> Unit)? = null,
) = ElevatedCard(
    modifier = modifier,
) {
    Column(
        modifier = Modifier
            .combinedClickable(
                enabled = (onClick != null) || (onLongClick != null),
                onLongClick = onLongClick,
                onClick = onClick ?: {},
            )
            .padding(MaterialTheme.dimensions.cardContentPadding),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = startTime,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(text = " - ")

            Text(
                text = endTime,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
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
                        .padding(start = 32.dp),
                )

                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    Icon(
                        imageVector = AppIcons.LocationOn,
                        contentDescription = null,
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
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
            )

            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(
            text = subjectName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge,
        )

        teachers.forEach { teacher ->
            key(teacher) {
                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Icon(
                            imageVector = AppIcons.Person,
                            contentDescription = null,
                        )
                    }

                    Text(
                        text = teacher,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
                    )
                }
            }
        }
    }
}

private data class LessonCardPreviewData(
    val subjectName: String,
    val type: String,
    val teachers: List<String>,
    val classrooms: List<String>,
    val startTime: String,
    val endTime: String,
)

@Suppress("StringLiteralDuplication", "MagicNumber")
private class LessonCardPreviewParameterProvider : PreviewParameterProvider<LessonCardPreviewData> {
    override val values = sequenceOf(
        LessonCardPreviewData(
            subjectName = "Интернет программирование",
            type = "Лабораторная работа",
            teachers = listOf("Кожухов Игорь Борисович"),
            classrooms = listOf("4212а", "4212б"),
            startTime = "09:00",
            endTime = "10:30",
        ),
        LessonCardPreviewData(
            subjectName = "Интернет программирование",
            type = "",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = "09:00",
            endTime = "10:30",
        ),
        LessonCardPreviewData(
            subjectName = "Интернет программирование программирование программирование программирование",
            type = "Лабораторная работа работа работа работа работа работа работа работа работа работа работа работа",
            teachers = listOf(
                "Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович",
                "Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович",
                "Кожухов Игорь Борисович Борисович Борисович Борисович Борисович Борисович Борисович Борисович",
            ),
            classrooms = listOf("4212а", "4212б", "4212в", "4212г", "4212д", "4212е", "4212ё", "4212ж", "4212з"),
            startTime = "09:00",
            endTime = "10:30",
        ),
    )
}

@AppPreviews
@Composable
private fun LessonCardPreview(
    @PreviewParameter(LessonCardPreviewParameterProvider::class) data: LessonCardPreviewData,
) = AppTheme {
    LessonCard(
        subjectName = data.subjectName,
        type = data.type,
        teachers = data.teachers,
        classrooms = data.classrooms,
        startTime = data.startTime,
        endTime = data.endTime,
    )
}
