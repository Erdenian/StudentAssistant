package ru.erdenian.studentassistant.ui.main.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.PreferenceDialogFragmentCompat
import org.joda.time.LocalTime

class TimePreferenceDialog : PreferenceDialogFragmentCompat() {

    private lateinit var selectedTime: LocalTime

    override fun getPreference() = super.getPreference() as TimePreference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timePreference = preference
        val time = timePreference.time

        return TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime = LocalTime(hourOfDay, minute)
                onClick(dialog, DialogInterface.BUTTON_POSITIVE)
            },
            time.hourOfDay,
            time.minuteOfHour,
            true
        ).apply {
            setButton(DialogInterface.BUTTON_NEGATIVE, timePreference.negativeButtonText, this@TimePreferenceDialog)
            setButton(DialogInterface.BUTTON_POSITIVE, timePreference.positiveButtonText, this@TimePreferenceDialog)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (!positiveResult) return
        val timePreference = preference
        if (timePreference.callChangeListener(selectedTime)) preference.time = selectedTime
    }

    companion object {
        fun newInstance(key: String) = TimePreferenceDialog().apply {
            arguments = bundleOf(ARG_KEY to key)
        }
    }
}
