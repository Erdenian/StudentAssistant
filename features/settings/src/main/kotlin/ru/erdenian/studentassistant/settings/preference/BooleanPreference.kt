package ru.erdenian.studentassistant.settings.preference

import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.erdenian.studentassistant.style.AppPreviews
import ru.erdenian.studentassistant.style.AppTheme

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

private class BooleanPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@AppPreviews
@Composable
private fun BooleanPreferencePreview(
    @PreviewParameter(BooleanPreviewParameterProvider::class) value: Boolean,
) = AppTheme {
    Surface {
        BooleanPreference(
            title = "Boolean Preference",
            description = "Description",
            value = value,
            onValueChange = {},
        )
    }
}
