package ru.erdenian.studentassistant.customviews

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
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see CheckBox
 * @see TextView
 * @since 0.2.6
 */
class CheckBoxWithText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val checkBox = AppCompatCheckBox(context)
    private val textView = AppCompatTextView(context)

    /**
     * Оборачивает [CheckBox.isChecked] и [CheckBox.setChecked]
     *
     * @since 0.2.6
     */
    var isChecked: Boolean
        get() = checkBox.isChecked
        set(checked) {
            checkBox.isChecked = checked
        }

    /**
     * Оборачивает [TextView.getText] и [TextView.setText]
     *
     * @since 0.2.6
     */
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

    /**
     * Оборачивает [CheckBox.setOnCheckedChangeListener]
     *
     * @since 0.2.6
     */
    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener?) {
        checkBox.setOnCheckedChangeListener(listener)
    }

    /**
     * @since 0.2.6
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        checkBox.isEnabled = enabled
        textView.isEnabled = enabled
    }
}
