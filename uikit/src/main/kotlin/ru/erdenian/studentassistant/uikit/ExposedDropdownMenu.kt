package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.text.InputType
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
