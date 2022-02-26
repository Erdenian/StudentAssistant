package com.erdenian.studentassistant.schedule.lessoneditor

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.max

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
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    val expanded = remember { mutableStateOf(false) }

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    val autocompleteItems = remember(items, textFieldValue.text) {
        if (textFieldValue.text.isBlank()) emptyList()
        else items.filter { it.contains(textFieldValue.text.trim(), ignoreCase = true) }
    }

    BaseAutoCompleteTextField(
        expanded = expanded,
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            if (value != newValue.text) {
                onValueChange(newValue.text)
                expanded.value = newValue.selection.collapsed && (newValue.text.length >= LENGTH_TO_EXPAND)
            }
        },
        items = autocompleteItems,
        onItemClick = { item ->
            textFieldValueState = textFieldValueState.copy(
                text = item,
                selection = TextRange(item.length),
                composition = null
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
        colors = colors
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
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    val expanded = remember { mutableStateOf(false) }

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    var autoCompleteRange by remember { mutableStateOf(IntRange(0, -1)) }
    val autoCompleteItems = remember(items, textFieldValue.text, autoCompleteRange) {
        val text = textFieldValue.text.substring(autoCompleteRange)
        if (text.isBlank()) emptyList()
        else items.filter { it.contains(text.trim(), ignoreCase = true) }
    }

    BaseAutoCompleteTextField(
        expanded = expanded,
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            if (value != newValue.text) {
                onValueChange(newValue.text)

                val currentItemStartIndex = newValue.text.lastIndexOf(',', startIndex = newValue.selection.min) + 1
                val dropCount = max(0, newValue.text.asSequence().drop(currentItemStartIndex).indexOfFirst { !it.isWhitespace() })
                val end = newValue.selection.min - 1
                autoCompleteRange = (currentItemStartIndex + dropCount)..end

                expanded.value = newValue.selection.collapsed && (autoCompleteRange.count() >= LENGTH_TO_EXPAND)
            }
        },
        items = autoCompleteItems,
        onItemClick = { item ->
            val itemWithComma = "$item, "
            textFieldValueState = textFieldValueState.copy(
                text = textFieldValue.text.replaceRange(autoCompleteRange, itemWithComma),
                selection = TextRange(autoCompleteRange.first + itemWithComma.length),
                composition = null
            )
            onValueChange(textFieldValueState.text)
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
        colors = colors
    )
}

@Composable
private fun BaseAutoCompleteTextField(
    expanded: MutableState<Boolean>,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    items: List<String>,
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
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { if (!it) expanded.value = it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.onFocusChanged { if (!it.isFocused) expanded.value = false },
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
            colors = colors
        )

        if (items.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            onItemClick(item)
                            expanded.value = false
                        }
                    ) {
                        Text(text = item)
                    }
                }
            }
        }
    }
}
