package ru.erdenian.studentassistant.uikit.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.utils.attr
import ru.erdenian.studentassistant.utils.colorAttr
import ru.erdenian.studentassistant.utils.getColorCompat

/**
 * Иконка и имя преподавателя.
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
        setTextAppearance(context, context.attr(R.attr.textAppearanceBody1).resourceId)
        setTextColor(context.colorAttr(android.R.attr.textColorSecondary))
    }

    var name: CharSequence?
        get() = textView.text
        set(value) {
            textView.text = value
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM

        addView(
            AppCompatImageView(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.CENTER_VERTICAL }
                setImageResource(R.drawable.ic_account)
                imageTintList = ColorStateList.valueOf(
                    context.getColorCompat(R.color.secondary_text)
                )
            }
        )
        addView(textView)

        context.withStyledAttributes(attrs, R.styleable.TeacherView, defStyleAttr) {
            name = getString(R.styleable.TeacherView_name)
        }
    }
}
