package ru.erdenian.studentassistant.ui.help

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.help.text = HtmlCompat.fromHtml(
            resources.openRawResource(R.raw.help).bufferedReader().readText(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> false
    }
}
