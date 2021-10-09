package ru.erdenian.studentassistant.uikit.views

import android.content.res.Configuration
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.utils.createOnEditorActionListener
import ru.erdenian.studentassistant.uikit.utils.update

@Composable
fun MultiAutoCompleteTextField(
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
    val currentKeyboardActions = rememberUpdatedState(keyboardActions)
    val adapter = LocalContext.current.let { context ->
        remember(context, items) { ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, items) }
    }

    AndroidView(
        factory = { context ->
            TextInputLayout(context).apply {
                AppCompatMultiAutoCompleteTextView(context).apply {
                    setOnEditorActionListener(createOnEditorActionListener(currentKeyboardActions))
                    addTextChangedListener { onValueChange(it?.toString() ?: "") }
                    setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
                }.let(this::addView)
            }
        },
        update = { view ->
            view.error = error
            view.isErrorEnabled = error.isNotBlank()

            view.isEnabled = enabled
            view.hint = label

            (view.editText as AppCompatMultiAutoCompleteTextView).apply {
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
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MultiAutoCompleteTextFieldPreview() = AppTheme {
    MultiAutoCompleteTextField(
        value = "Text",
        items = emptyList(),
        onValueChange = {}
    )
}
