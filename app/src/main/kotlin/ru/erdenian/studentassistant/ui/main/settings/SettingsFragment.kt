package ru.erdenian.studentassistant.ui.main.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.uikit.databinding.FragmentSettingsBinding

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        fun <T : Preference> requirePreference(key: CharSequence) = checkNotNull(findPreference<T>(key))
        val context = requireContext()

        requirePreference<TimePreference>(context.getString(R.string.pk_default_start_time)).apply {
            setOnPreferenceChangeListener { _, newValue ->
                true
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        val fragment = when (preference) {
            is TimePreference -> TimePreferenceDialog.newInstance(preference.key)
            else -> return super.onDisplayPreferenceDialog(preference)
        }
        @Suppress("DEPRECATION")
        fragment.setTargetFragment(this@SettingsFragment, 0)
        fragment.show(parentFragmentManager, null)
    }
}
