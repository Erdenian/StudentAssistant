package ru.erdenian.studentassistant.uikit.view

import android.content.Context
import android.content.res.Configuration
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
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
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.uikit.utils.createOnEditorActionListener
import ru.erdenian.studentassistant.uikit.utils.update

private class ExposedDropdownMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.exposedDropdownMenuStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val autoCompleteTextView = MaterialAutoCompleteTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    init {
        addView(autoCompleteTextView)
    }

    var text: CharSequence?
        get() = autoCompleteTextView.text?.toString()
        set(value) = autoCompleteTextView.setText(value, false)

    fun setAdapter(adapter: Adapter) = autoCompleteTextView.setAdapter(adapter)

    var textChangedListener: TextWatcher? = null
        set(value) {
            if (value != null) autoCompleteTextView.addTextChangedListener(value)
            else autoCompleteTextView.removeTextChangedListener(field)
            field = value
        }

    class Adapter(
        context: Context,
        items: List<String> = emptyList()
    ) : ArrayAdapter<CharSequence>(context, R.layout.dropdown_menu_popup_item) {

        var items: List<String> = items
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItem(position: Int) = items[position]
        override fun getItemId(position: Int) = items[position].hashCode().toLong()
        override fun getCount() = items.size
    }
}

@Composable
fun ExposedDropdownMenu(
    value: String,
    items: List<String>,
    onValueChange: (newValue: String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String = "",
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
        // Not including items to remember's keys as it can be updated on the fly
        remember(context) { ExposedDropdownMenu.Adapter(context, items) }
    }
    var viewToFocus by remember { mutableStateOf<View?>(null) }

    AndroidView(
        factory = { context ->
            ExposedDropdownMenu(context).apply {
                checkNotNull(editText).apply {
                    setOnEditorActionListener(createOnEditorActionListener(currentKeyboardActions))
                    viewToFocus = this
                }
                setAdapter(adapter)
                textChangedListener = textWatcher
            }
        },
        update = { view ->
            checkNotNull(view.editText).apply {
                val selectionStart = selectionStart
                val selectionEnd = selectionEnd

                update(singleLine, keyboardOptions, textWatcher)
                if (readOnly) inputType = InputType.TYPE_NULL
                this.isSingleLine = singleLine
                this.maxLines = maxLines

                setSelection(selectionStart, selectionEnd)
            }

            view.isEnabled = enabled
            view.hint = label

            if (view.text != value) view.text = value
            if (adapter.items != items) adapter.items = items
        },
        modifier = modifier
            .onFocusChanged { if (it.isFocused) checkNotNull(viewToFocus).requestFocus() }
            .focusable()
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExposedDropdownMenuPreview() = AppTheme {
    ExposedDropdownMenu(
        value = "Text",
        items = emptyList(),
        onValueChange = {}
    )
}
