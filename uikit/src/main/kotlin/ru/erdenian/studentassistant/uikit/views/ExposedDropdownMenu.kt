package ru.erdenian.studentassistant.uikit.views

import android.content.Context
import android.content.res.Configuration
import android.text.InputType
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
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.utils.createOnEditorActionListener
import ru.erdenian.studentassistant.uikit.utils.update

class ExposedDropdownMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.exposedDropdownMenuStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val autoCompleteTextView = MaterialAutoCompleteTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    init {
        addView(autoCompleteTextView)

        autoCompleteTextView.addTextChangedListener { text ->
            val string = text?.toString() ?: ""
            val adapter = autoCompleteTextView.adapter as Adapter<*>?
            onTextChangedListener?.invoke(string, adapter?.strings?.indexOf(string) ?: -1)
        }
    }

    var text: CharSequence?
        get() = autoCompleteTextView.text?.toString()
        set(value) = autoCompleteTextView.setText(value, false)

    fun <T> setAdapter(adapter: Adapter<T>) = autoCompleteTextView.setAdapter(adapter)

    var onTextChangedListener: ((text: String, position: Int) -> Unit)? = null

    class Adapter<T>(
        context: Context,
        items: List<T> = emptyList(),
        private val stringSelector: (T) -> CharSequence
    ) : ArrayAdapter<CharSequence>(context, R.layout.dropdown_menu_popup_item) {

        var items: List<T> = items
            set(value) {
                field = value
                strings = items.map(stringSelector)
                notifyDataSetChanged()
            }

        internal var strings: List<CharSequence> = items.map(stringSelector)
            private set

        override fun getItem(position: Int) = strings[position]
        override fun getItemId(position: Int) = strings[position].hashCode().toLong()
        override fun getCount() = strings.size
    }
}

@Composable
fun ExposedDropdownMenu(
    value: String,
    items: List<String>,
    onValueChange: (value: String, index: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) = ExposedDropdownMenu(
    value = value,
    items = items,
    stringSelector = { it },
    onValueChange = { newValue, newIndex, _ -> onValueChange(newValue, newIndex) },
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    label = label,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = singleLine,
    maxLines = maxLines,
)

@Composable
fun <T : Any> ExposedDropdownMenu(
    value: String,
    items: List<T>,
    stringSelector: (T) -> String,
    onValueChange: (newValue: String, newIndex: Int, newItem: T?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    val currentValue by rememberUpdatedState(value)
    val currentKeyboardActions = rememberUpdatedState(keyboardActions)
    val adapter = LocalContext.current.let { context ->
        // Not including items to remember's keys as it can be updated on the fly
        remember(context, stringSelector) { ExposedDropdownMenu.Adapter(context, items, stringSelector) }
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
                onTextChangedListener = { text, index ->
                    if (text != currentValue) onValueChange(text, index, items.getOrNull(index))
                }
            }
        },
        update = { view ->
            checkNotNull(view.editText).apply {
                val selectionStart = selectionStart
                val selectionEnd = selectionEnd

                update(singleLine, keyboardOptions)
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
        onValueChange = { _, _ -> }
    )
}
