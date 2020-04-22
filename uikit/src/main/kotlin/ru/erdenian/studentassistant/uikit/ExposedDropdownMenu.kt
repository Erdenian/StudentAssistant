package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.core.content.withStyledAttributes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class ExposedDropdownMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.exposedDropdownMenuStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val autoCompleteTextView = MaterialAutoCompleteTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private var stringMap: Map<CharSequence, Any?> = emptyMap()
    private var textWatcher: TextWatcher? = null

    init {
        addView(autoCompleteTextView)

        context.withStyledAttributes(attrs, R.styleable.ExposedDropdownMenu, defStyleAttr) {
            val editable = getBoolean(R.styleable.ExposedDropdownMenu_editable, false)
            if (!editable) autoCompleteTextView.inputType = InputType.TYPE_NULL
        }
    }

    var text: CharSequence?
        get() = autoCompleteTextView.text?.toString()
        set(value) = autoCompleteTextView.setText(value, false)

    fun <T> setAdapter(adapter: Adapter<T>, stringSelector: (T) -> CharSequence) {
        stringMap = adapter.items.associateBy { stringSelector(it) }
        autoCompleteTextView.setAdapter(ArrayAdapter(context, R.layout.dropdown_menu_popup_item, stringMap.values.toList()))

        autoCompleteTextView.removeTextChangedListener(textWatcher)
        adapter.onTextChanged?.let { onTextChanged ->
            autoCompleteTextView.addTextChangedListener { text ->
                val string = text?.toString() ?: ""
                @Suppress("UNCHECKED_CAST")
                onTextChanged.invoke(string, stringMap[string] as T?)
            }
        }
    }

    fun setAdapter(adapter: Adapter<String>) {
        stringMap = emptyMap()
        autoCompleteTextView.setAdapter(ArrayAdapter(context, R.layout.dropdown_menu_popup_item, adapter.items))

        autoCompleteTextView.removeTextChangedListener(textWatcher)
        adapter.onTextChanged?.let { onTextChanged ->
            autoCompleteTextView.addTextChangedListener { text ->
                val string = text?.toString() ?: ""
                onTextChanged.invoke(string, if (adapter.items.contains(string)) string else null)
            }
        }
    }

    interface Adapter<T> {
        val items: List<T>
        val onTextChanged: ((text: String, item: T?) -> Unit)? get() = null
    }
}
