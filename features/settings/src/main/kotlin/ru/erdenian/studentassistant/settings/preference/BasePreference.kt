package ru.erdenian.studentassistant.settings.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.utils.AppPreviews

@Composable
internal fun BasePreference(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    contentEnd: (@Composable () -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 73.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .padding(horizontal = 16.dp),
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
                    .weight(1.0f, false),
            ) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = description,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                )
            }

            if (contentEnd != null) {
                Box(modifier = Modifier.padding(end = 16.dp)) { contentEnd() }
            }
        }
    }
}

private class BasePreferencePreviewParameterProvider : PreviewParameterProvider<Pair<String, String>> {
    override val values = sequenceOf(
        "Title" to "Description",
        "Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title" to
            "Description Description Description Description Description Description Description Description",
    )
}

@AppPreviews
@Composable
private fun BasePreferencePreview(
    @PreviewParameter(BasePreferencePreviewParameterProvider::class) texts: Pair<String, String>,
) = AppTheme {
    Surface {
        BasePreference(
            title = texts.first,
            icon = rememberVectorPainter(AppIcons.Timer),
            description = texts.second,
            onClick = {},
        )
    }
}

@AppPreviews
@Composable
private fun BasePreferenceWithContentEndPreview(
    @PreviewParameter(BasePreferencePreviewParameterProvider::class) texts: Pair<String, String>,
) = AppTheme {
    Surface {
        BasePreference(
            title = texts.first,
            icon = rememberVectorPainter(AppIcons.Timer),
            description = texts.second,
            contentEnd = {
                Box(
                    Modifier
                        .size(24.dp)
                        .background(Color.Green),
                )
            },
            onClick = {},
        )
    }
}
