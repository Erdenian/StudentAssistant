package ru.erdenian.studentassistant.ui.help

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentHelpBinding

class HelpFragment : Fragment(R.layout.fragment_help) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentHelpBinding.bind(view)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
        }

        binding.help.text = HtmlCompat.fromHtml(
            resources.openRawResource(R.raw.help).bufferedReader().readText(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}
