package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_homeworks.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView

class HomeworksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        setSupportActionBar(toolbar)
        initializeDrawerAndNavigationView(toolbar)

        activity_homeworks_add_homework.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
