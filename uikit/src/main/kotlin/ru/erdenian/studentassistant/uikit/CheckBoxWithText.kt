package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView

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

    private val checkBox = AppCompatCheckBox(context)
    private val textView = AppCompatTextView(context).apply { gravity = Gravity.CENTER_HORIZONTAL }

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

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.CheckBoxWithText, defStyleAttr, 0
            )
            try {
                isChecked = typedArray.getBoolean(R.styleable.CheckBoxWithText_checked, false)
                text = typedArray.getString(R.styleable.CheckBoxWithText_text)
            } finally {
                typedArray.recycle()
            }
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