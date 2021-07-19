package ru.erdenian.studentassistant.uikit.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun CheckBoxWithText(
    checked: Boolean,
    text: String,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
        .padding(4.dp)
        .clickable { if (enabled) onCheckedChange?.invoke(!checked) }
        .then(modifier)
) {
    Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
    Text(text = text, maxLines = 1)
}

@Preview(name = "Short text")
@Composable
private fun CheckBoxWithTextPreviewShort() = CheckBoxWithText(true, "1", null)

@Preview(name = "Medium text")
@Composable
private fun CheckBoxWithTextPreviewMedium() = CheckBoxWithText(true, "Text", null)

@Preview(name = "Long text")
@Composable
private fun CheckBoxWithTextPreviewLong() = CheckBoxWithText(true, "Long text", null)

@Preview(name = "Disabled")
@Composable
private fun CheckBoxWithTextPreviewDisabled() = CheckBoxWithText(true, "Disabled", null, enabled = false)
