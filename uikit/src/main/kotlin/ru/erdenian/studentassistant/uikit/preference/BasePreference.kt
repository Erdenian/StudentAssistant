package ru.erdenian.studentassistant.uikit.preference

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.uikit.style.AppIcons
import ru.erdenian.studentassistant.uikit.style.AppTheme

@Composable
internal fun BasePreference(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 73.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Text(text = title)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.body2
                )
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
        description = LocalTime.now().toString(),
        onClick = {}
    )
}
