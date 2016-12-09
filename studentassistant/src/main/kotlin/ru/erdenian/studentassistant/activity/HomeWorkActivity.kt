package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView

class HomeWorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        setSupportActionBar(toolbar_with_spinner)
        initializeDrawerAndNavigationView(toolbar_with_spinner)

        view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
