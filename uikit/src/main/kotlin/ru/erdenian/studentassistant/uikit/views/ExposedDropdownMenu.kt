package ru.erdenian.studentassistant.uikit.views

import android.content.Context
import android.content.res.Configuration
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.withStyledAttributes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.uikit.style.AppTheme

class ExposedDropdownMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.exposedDropdownMenuStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    companion object {
        fun <T> createAdapter(context: Context, items: List<T> = emptyList(), stringSelector: (T) -> CharSequence) =
            Adapter(context, items, stringSelector)

        fun createAdapter(context: Context, items: List<String> = emptyList()) =
            Adapter(context, items) { it }
    }

    private val autoCompleteTextView = MaterialAutoCompleteTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    init {
        addView(autoCompleteTextView)

        context.withStyledAttributes(attrs, R.styleable.ExposedDropdownMenu, defStyleAttr) {
            val editable = getBoolean(R.styleable.ExposedDropdownMenu_editable, false)
            if (!editable) autoCompleteTextView.inputType = InputType.TYPE_NULL
            autoCompleteTextView.isSingleLine = getBoolean(R.styleable.ExposedDropdownMenu_singleLine, false)
            autoCompleteTextView.inputType = getInt(R.styleable.ExposedDropdownMenu_android_inputType, 0)
        }

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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    val rememberedKeyboardActions = rememberUpdatedState(keyboardActions)
    val adapter = LocalContext.current.let { remember { ExposedDropdownMenu.createAdapter(it) } }

    AndroidView(
        factory = { context ->
            ExposedDropdownMenu(context).apply {
                checkNotNull(editText).setOnEditorActionListener(createOnEditorActionListener(rememberedKeyboardActions))
                setAdapter(adapter)
                onTextChangedListener = { text, _ -> onValueChange(text) }
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
    )
}

private fun createOnEditorActionListener(keyboardActions: State<KeyboardActions>): TextView.OnEditorActionListener {
    val emptyKeyboardActionScope = object : KeyboardActionScope {
        override fun defaultKeyboardAction(imeAction: ImeAction) = Unit
    }

    return TextView.OnEditorActionListener { _, actionId, _ ->
        val event = when (actionId) {
            EditorInfo.IME_ACTION_DONE -> keyboardActions.value.onDone
            EditorInfo.IME_ACTION_GO -> keyboardActions.value.onGo
            EditorInfo.IME_ACTION_NEXT -> keyboardActions.value.onNext
            EditorInfo.IME_ACTION_PREVIOUS -> keyboardActions.value.onPrevious
            EditorInfo.IME_ACTION_SEARCH -> keyboardActions.value.onSearch
            EditorInfo.IME_ACTION_SEND -> keyboardActions.value.onSend
            else -> null
        }

        if (event != null) {
            event(emptyKeyboardActionScope)
            true
        } else false
    }
}

private fun EditText.update(singleLine: Boolean, keyboardOptions: KeyboardOptions) {
    fun hasFlag(bits: Int, flag: Int) = (bits and flag) == flag

    this.imeOptions = when (keyboardOptions.imeAction) {
        ImeAction.Default -> if (singleLine) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_UNSPECIFIED
        ImeAction.None -> EditorInfo.IME_ACTION_NONE
        ImeAction.Go -> EditorInfo.IME_ACTION_GO
        ImeAction.Next -> EditorInfo.IME_ACTION_NEXT
        ImeAction.Previous -> EditorInfo.IME_ACTION_PREVIOUS
        ImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
        ImeAction.Send -> EditorInfo.IME_ACTION_SEND
        ImeAction.Done -> EditorInfo.IME_ACTION_DONE
        else -> error("invalid ImeAction")
    }

    when (keyboardOptions.keyboardType) {
        KeyboardType.Text -> this.inputType = InputType.TYPE_CLASS_TEXT
        KeyboardType.Ascii -> {
            this.inputType = InputType.TYPE_CLASS_TEXT
            this.imeOptions = this.imeOptions or EditorInfo.IME_FLAG_FORCE_ASCII
        }
        KeyboardType.Number -> this.inputType = InputType.TYPE_CLASS_NUMBER
        KeyboardType.Phone -> this.inputType = InputType.TYPE_CLASS_PHONE
        KeyboardType.Uri -> this.inputType = InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_URI
        KeyboardType.Email -> this.inputType = InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        KeyboardType.Password -> this.inputType = InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        KeyboardType.NumberPassword -> this.inputType = InputType.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        else -> error("Invalid Keyboard Type")
    }

    if (!singleLine && hasFlag(this.inputType, InputType.TYPE_CLASS_TEXT)) {
        this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE

        if (keyboardOptions.imeAction == ImeAction.Default) {
            this.imeOptions = this.imeOptions or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        }
    }

    if (hasFlag(this.inputType, InputType.TYPE_CLASS_TEXT)) {
        when (keyboardOptions.capitalization) {
            KeyboardCapitalization.Characters -> this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            KeyboardCapitalization.Words -> this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            KeyboardCapitalization.Sentences -> this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            else -> Unit
        }

        if (keyboardOptions.autoCorrect) this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
    }

    this.imeOptions = this.imeOptions or EditorInfo.IME_FLAG_NO_FULLSCREEN
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
