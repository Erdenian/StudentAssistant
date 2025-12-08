package ru.erdenian.studentassistant.uikit.view

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.utils.AppPreviews

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
        Text(
            text = subjectName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimensions.dividerPaddingVertical))

        Text(
            text = description,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(RS.hc_deadline, deadline),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private data class HomeworkCardPreviewData(
    val subjectName: String,
    val description: String,
    val deadline: String,
)

@Suppress("StringLiteralDuplication", "MagicNumber")
private class HomeworkCardPreviewParameterProvider : PreviewParameterProvider<HomeworkCardPreviewData> {
    override val values = sequenceOf(
        HomeworkCardPreviewData(
            subjectName = "Интернет программирование",
            description = "Лабораторная работа",
            deadline = "21.08.2021",
        ),
        HomeworkCardPreviewData(
            subjectName = "Интернет программирование программирование программирование программирование",
            description = "Лабораторная работа" + " работа".repeat(100),
            deadline = "21.08.2021",
        ),
        HomeworkCardPreviewData(
            subjectName = "Интернет программирование программирование программирование программирование",
            description = "Лабораторная работа" + "\nЛабораторная работа".repeat(100),
            deadline = "21.08.2021",
        ),
    )
}

@AppPreviews
@Composable
private fun HomeworkCardPreview(
    @PreviewParameter(HomeworkCardPreviewParameterProvider::class) data: HomeworkCardPreviewData,
) = AppTheme {
    HomeworkCard(
        subjectName = data.subjectName,
        description = data.description,
        deadline = data.deadline,
    )
}
