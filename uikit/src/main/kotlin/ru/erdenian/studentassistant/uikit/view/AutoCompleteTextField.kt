package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.utils.createOnEditorActionListener
import ru.erdenian.studentassistant.uikit.utils.update

@Composable
fun AutoCompleteTextField(
    value: String,
    items: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String = "",
    error: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    val currentValue by rememberUpdatedState(value)
    val currentKeyboardActions = rememberUpdatedState(keyboardActions)
    val adapter = LocalContext.current.let { context ->
        remember(context, items) { ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, items) }
    }
    var viewToFocus by remember { mutableStateOf<View?>(null) }

    AndroidView(
        factory = { context ->
            TextInputLayout(context).apply {
                MaterialAutoCompleteTextView(context).apply {
                    setOnEditorActionListener(createOnEditorActionListener(currentKeyboardActions))

                    var isInitialized = false
                    addTextChangedListener { editable ->
                        // Ignore the first call to the listener
                        // because it is called with an empty string during initialization of inputType
                        if (!isInitialized && editable.isNullOrEmpty()) return@addTextChangedListener
                        isInitialized = true

                        val newValue = editable?.toString() ?: ""
                        if (newValue != currentValue) onValueChange(newValue)
                    }
                }
                    .also { viewToFocus = it }
                    .also(this::addView)
            }
        },
        update = { view ->
            view.error = error
            view.isErrorEnabled = error.isNotBlank()

            view.isEnabled = enabled
            view.hint = label

            (view.editText as MaterialAutoCompleteTextView).apply {
                val selectionStart = selectionStart
                val selectionEnd = selectionEnd

                update(singleLine, keyboardOptions)
                if (readOnly) inputType = InputType.TYPE_NULL
                this.isSingleLine = singleLine
                this.maxLines = maxLines

                if (text.toString() != value) setText(value)
                if (this.adapter != adapter) setAdapter(adapter)

                setSelection(selectionStart, selectionEnd)
            }
        },
        modifier = modifier
            .onFocusChanged { if (it.isFocused) checkNotNull(viewToFocus).requestFocus() }
            .focusable()
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AutoCompleteTextFieldPreview() = AppTheme {
    AutoCompleteTextField(
        value = "Text",
        items = emptyList(),
        onValueChange = {}
    )
}
