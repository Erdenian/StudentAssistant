package com.erdenian.studentassistant.settings.preference

import android.content.res.Configuration
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme

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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BasePreferencePreview() = AppTheme {
    BasePreference(
        title = "Title",
        icon = rememberVectorPainter(AppIcons.Timer),
        description = "Description",
        onClick = {},
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    )
}

@Preview
@Composable
private fun BasePreferenceLongPreview() = AppTheme {
    BasePreference(
        title = "Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title",
        icon = rememberVectorPainter(AppIcons.Timer),
        description = "Description Description Description Description Description Description Description Description",
        onClick = {},
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    )
}

@Preview
@Composable
private fun BasePreferenceWithContentEndPreview() = AppTheme {
    BasePreference(
        title = "Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title",
        icon = rememberVectorPainter(AppIcons.Timer),
        description = "Description Description Description Description Description Description Description Description",
        contentEnd = {
            Box(
                Modifier
                    .size(24.dp)
                    .background(Color.Green),
            )
        },
        onClick = {},
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    )
}

@Preview
@Composable
private fun BasePreferenceWithContentEndLongPreview() = AppTheme {
    BasePreference(
        title = "Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title",
        icon = rememberVectorPainter(AppIcons.Timer),
        description = "Description Description Description Description Description Description Description Description",
        contentEnd = {
            Box(
                Modifier
                    .size(24.dp)
                    .background(Color.Green),
            )
        },
        onClick = {},
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    )
}
