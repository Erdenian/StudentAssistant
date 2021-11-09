package ru.erdenian.studentassistant.uikit.view

import android.content.res.Configuration
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import ru.erdenian.studentassistant.style.AppTheme
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
    val textWatcher = run {
        val currentValue by rememberUpdatedState(value)
        val currentOnValueChange by rememberUpdatedState(onValueChange)
        remember {
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    val newValue = s?.toString() ?: ""
                    if (newValue != currentValue) currentOnValueChange(newValue)
                }
            }
        }
    }

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
                    addTextChangedListener(textWatcher)
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

                update(singleLine, keyboardOptions, textWatcher)
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
