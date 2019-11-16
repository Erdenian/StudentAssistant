package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.getResId

/**
 * Иконка и имя преподавателя
 *
 * @see android.widget.ImageView
 * @see TextView
 *
 * @version 1.0.0
 * @author Ilya Solovyov
 * @since 0.2.6
 */
class TeacherView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val textView = AppCompatTextView(context).apply {
        with(context) {
            setTextAppearance(this, getResId(R.attr.textAppearanceBody1))
            setTextColor(getColorCompat(getResId(android.R.attr.textColorSecondary)))
        }
    }

    var name: CharSequence?
        get() = textView.text
        set(value) {
            textView.text = value
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM

        addView(AppCompatImageView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER_VERTICAL }
            setImageResource(R.drawable.ic_account)
            imageTintList = ColorStateList.valueOf(
                context.getColorCompat(R.color.secondary_text)
            )
        })
        addView(textView)

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.TeacherView, defStyleAttr, 0
            )
            try {
                name = typedArray.getString(R.styleable.TeacherView_name)
            } finally {
                typedArray.recycle()
            }
        }
    }
}
