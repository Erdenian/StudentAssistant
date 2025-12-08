package ru.erdenian.studentassistant.schedule.lessoneditor.composable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.utils.AppPreviews
import ru.erdenian.studentassistant.utils.toSingleLine

private const val DELIMITER = ','
private const val LENGTH_TO_EXPAND = 2

@Composable
internal fun AutoCompleteTextField(
    value: String,
    items: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    val autocompleteItems = remember(items, textFieldValue.text) {
        val text = textFieldValue.text.trim()
        if (text.length < LENGTH_TO_EXPAND) {
            emptyList()
        } else {
            items.asSequence()
                .filter { it.contains(text, ignoreCase = true) && (it.length > text.length) }
                .sorted()
                .toList()
        }
    }

    BaseAutoCompleteTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            onValueChange(newValue.text)
        },
        autoCompleteItems = autocompleteItems,
        onItemClick = { item ->
            textFieldValueState = textFieldValueState.copy(
                text = item,
                selection = TextRange(item.length),
                composition = null,
            )
            onValueChange(item)
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}

@Composable
internal fun MultiAutoCompleteTextField(
    value: String,
    items: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    var autoCompleteRange by remember { mutableStateOf(IntRange(0, -1)) }
    fun recalculateAutoCompleteRange(newValue: TextFieldValue) {
        val currentItemStartIndex = newValue.text.lastIndexOf(DELIMITER, startIndex = newValue.selection.min - 1) + 1
        val currentItemEndIndex = (newValue.text.indexOf(DELIMITER, startIndex = newValue.selection.min) - 1)
            .takeIf { it >= 0 }
            ?: newValue.text.lastIndex
        autoCompleteRange = currentItemStartIndex..currentItemEndIndex
    }

    val autoCompleteItems = remember(items, textFieldValue.text, autoCompleteRange) {
        val text = textFieldValue.text.substring(autoCompleteRange).trim()
        if (text.length < LENGTH_TO_EXPAND) {
            emptyList()
        } else {
            val enteredItems = textFieldValue.text
                .toSingleLine()
                .split(DELIMITER)
                .asSequence()
                .map(String::trim)
                .filter(String::isNotBlank)
                .toSet()
            items.asSequence()
                .filter { it !in enteredItems }
                .filter { it.contains(text, ignoreCase = true) && (it.length > text.length) }
                .sorted()
                .toList()
        }
    }

    BaseAutoCompleteTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            onValueChange(newValue.text)
            recalculateAutoCompleteRange(newValue)
        },
        autoCompleteItems = autoCompleteItems,
        onItemClick = { item ->
            val start = if (autoCompleteRange.first == 0) "" else " "
            val end = if (autoCompleteRange.last == textFieldValueState.text.lastIndex) ", " else ""
            val itemWithComma = start + item + end
            textFieldValueState = textFieldValueState.copy(
                text = textFieldValue.text.replaceRange(autoCompleteRange, itemWithComma),
                selection = TextRange(autoCompleteRange.first + itemWithComma.length),
                composition = null,
            )
            onValueChange(textFieldValueState.text)
            recalculateAutoCompleteRange(textFieldValueState)
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}

@Composable
private fun BaseAutoCompleteTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    autoCompleteItems: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }

    run {
        val currentAutoCompleteItems by rememberUpdatedState(autoCompleteItems)
        val currentValue by rememberUpdatedState(value)
        LaunchedEffect(Unit) {
            snapshotFlow { currentValue }
                .collect { expanded = hasFocus && it.selection.collapsed && currentAutoCompleteItems.isNotEmpty() }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (!it) expanded = false },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.hasFocus
                    expanded = false
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            autoCompleteItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        onItemClick(item)
                        expanded = false
                    },
                )
            }
        }
    }
}

private data class AutoCompleteTextFieldPreviewState(
    val value: String,
    val enabled: Boolean,
)

private class AutoCompleteTextFieldPreviewParameterProvider :
    PreviewParameterProvider<AutoCompleteTextFieldPreviewState> {
    override val values = sequenceOf(
        AutoCompleteTextFieldPreviewState(value = "Text", enabled = true),
        AutoCompleteTextFieldPreviewState(value = "Text", enabled = false),
        AutoCompleteTextFieldPreviewState(value = "", enabled = true),
        AutoCompleteTextFieldPreviewState(
            value = "Very very very very very very very very very very very very very long text",
            enabled = true,
        ),
    )
}

@AppPreviews
@Composable
private fun AutoCompleteTextFieldPreview(
    @PreviewParameter(AutoCompleteTextFieldPreviewParameterProvider::class) state: AutoCompleteTextFieldPreviewState,
) = AppTheme {
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            AutoCompleteTextField(
                value = state.value,
                items = emptyList(),
                onValueChange = {},
                enabled = state.enabled,
                label = { Text(text = "AutoCompleteTextField") },
            )
            Spacer(modifier = Modifier.size(16.dp))
            MultiAutoCompleteTextField(
                value = state.value,
                items = emptyList(),
                onValueChange = {},
                enabled = state.enabled,
                label = { Text(text = "MultiAutoCompleteTextField") },
            )
        }
    }
}
