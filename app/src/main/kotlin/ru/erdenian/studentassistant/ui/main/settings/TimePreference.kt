package ru.erdenian.studentassistant.ui.main.settings

import android.content.Context
import android.util.AttributeSet

import androidx.preference.DialogPreference
import androidx.preference.R
import org.joda.time.LocalTime

class TimePreference @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle,
    defStyleRes: Int = android.R.attr.dialogPreferenceStyle
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    fun getPersistedTime() = getPersistedInt(DEFAULT_TIME_MILLIS).let(LocalTime.MIDNIGHT::plusMillis)

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
