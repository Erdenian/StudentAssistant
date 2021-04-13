package ru.erdenian.studentassistant.ui.main.settings

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.preference.DialogPreference
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R

class TimePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private var defaultTimeMillis = DEFAULT_TIME_MILLIS

    init {
        context.withStyledAttributes(attrs, R.styleable.Preference, defStyleAttr) {
            defaultTimeMillis = getInt(R.styleable.Preference_defaultValue, DEFAULT_TIME_MILLIS)
        }
        summary = getPersistedTime().toString("HH:mm")
    }

    fun getPersistedTime() = getPersistedInt(defaultTimeMillis).let(LocalTime.MIDNIGHT::plusMillis)

    fun persistTime(time: LocalTime) {
        persistInt(time.millisOfDay)
        notifyChanged()
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)
        summary = getPersistedTime().toString("HH:mm")
    }

    companion object {
        private const val DEFAULT_TIME_MILLIS = 0
    }
}
