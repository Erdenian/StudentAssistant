package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes

/**
 * Чекбокс с текстом под ним.
 *
 * @see CheckBox
 * @see TextView
 *
 * @version 1.0.0
 * @author Ilya Solovyov
 * @since 0.2.6
 */
class CheckBoxWithText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val checkBox = AppCompatCheckBox(context).apply {
        // Костыль для того, чтобы убрать пустое пространство под текст
        val size = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            @Suppress("MagicNumber") 32.0f,
            context.resources.displayMetrics
        ).toInt()
        layoutParams = LayoutParams(size, size)
    }
    private val textView = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_HORIZONTAL
        setOnClickListener { checkBox.toggle() }
    }

    var isChecked: Boolean
        get() = checkBox.isChecked
        set(checked) {
            checkBox.isChecked = checked
        }

    var text: CharSequence?
        get() = textView.text
        set(text) {
            textView.text = text
        }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        addView(checkBox)
        addView(textView)

        context.withStyledAttributes(attrs, R.styleable.CheckBoxWithText, defStyleAttr) {
            isChecked = getBoolean(R.styleable.CheckBoxWithText_checked, false)
            text = getString(R.styleable.CheckBoxWithText_text)
        }
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener?) {
        checkBox.setOnCheckedChangeListener(listener)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        checkBox.isEnabled = enabled
        textView.isEnabled = enabled
    }
}
