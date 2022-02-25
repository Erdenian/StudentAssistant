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
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.uikit.layout.StartEndRow

@Composable
internal fun BasePreference(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    contentEnd: (@Composable () -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 73.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .padding(horizontal = 16.dp)
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null
                )
            }
        }

        StartEndRow(
            verticalAlignment = Alignment.CenterVertically,
            contentStart = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = description,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            },
            contentEnd = contentEnd ?: {}
        )
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
        modifier = Modifier.background(MaterialTheme.colors.background)
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
        modifier = Modifier.background(MaterialTheme.colors.background)
    )
}
