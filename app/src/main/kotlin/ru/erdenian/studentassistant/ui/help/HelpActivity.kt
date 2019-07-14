package ru.erdenian.studentassistant.ui.help

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireViewByIdCompat<TextView>(R.id.ahlp_help).text = HtmlCompat.fromHtml(
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
