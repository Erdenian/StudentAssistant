package ru.erdenian.studentassistant.settings.preference

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
internal fun BooleanPreference(
    title: String,
    description: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) = BasePreference(
    title = title,
    description = description,
    icon = icon,
    contentEnd = { Switch(checked = value, onCheckedChange = onValueChange) },
    onClick = { onValueChange(!value) },
    modifier = modifier,
)
