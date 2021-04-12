package ru.erdenian.studentassistant.ui.main.settings

import android.content.Context
import android.view.View
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.preference.PreferenceDialogFragmentCompat
import org.joda.time.LocalTime

class TimePreferenceDialog : PreferenceDialogFragmentCompat() {

    private lateinit var timepicker: TimePicker

    override fun onCreateDialogView(context: Context?): View {
        timepicker = TimePicker(context)
        return timepicker
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        val time = (preference as TimePreference).getPersistedTime()
        timepicker.setIs24HourView(true)
        timepicker.time = time
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val time = timepicker.time
            (preference as TimePreference).persistTime(time)
            preference.summary = time.toString("HH:mm")
        }
    }

    private var TimePicker.time: LocalTime
        get() =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) LocalTime(hour, minute)
            else LocalTime(currentHour, currentMinute)
        set(value) =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hour = value.hourOfDay
                minute = value.minuteOfHour
            } else {
                currentHour = value.hourOfDay
                currentMinute = value.minuteOfHour
            }

    companion object {
        fun newInstance(key: String) = TimePreferenceDialog().apply {
            arguments = bundleOf(ARG_KEY to key)
        }
    }
}
