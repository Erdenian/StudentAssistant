package ru.erdenian.studentassistant.extensions

import android.app.Activity
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import kotlinx.android.synthetic.main.navigation_view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.AlarmEditorActivity
import ru.erdenian.studentassistant.activity.HomeworksActivity
import ru.erdenian.studentassistant.activity.ScheduleActivity
import ru.erdenian.studentassistant.schedule.ScheduleManager

fun Activity.initializeDrawerAndNavigationView(toolbar: Toolbar) {
    val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

    val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    navigation_view.setCheckedItem(R.id.nav_schedule)

    navigation_view.menu.findItem(R.id.nav_homeworks).isEnabled = ScheduleManager.hasLessons
    navigation_view.menu.findItem(R.id.nav_alarm).isEnabled = ScheduleManager.hasLessons

    navigation_view.setNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.nav_schedule -> if (this !is ScheduleActivity) {
                startActivity<ScheduleActivity>()
                finish()
            }
            R.id.nav_homeworks -> if (this !is HomeworksActivity) {
                startActivity<HomeworksActivity>()
                finish()
            }
            R.id.nav_alarm -> if (this !is AlarmEditorActivity) {
                startActivity<AlarmEditorActivity>()
                finish()
            }
            R.id.nav_settings -> toast(R.string.nav_settings)
            R.id.nav_help -> toast(R.string.nav_help)
            else -> throw IllegalArgumentException("Неизвестный id: ${it.itemId}")
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    when (this) {
        is ScheduleActivity -> navigation_view.setCheckedItem(R.id.nav_schedule)
        is HomeworksActivity -> navigation_view.setCheckedItem(R.id.nav_homeworks)
        is AlarmEditorActivity -> navigation_view.setCheckedItem(R.id.nav_alarm)
        else -> Log.wtf(this.javaClass.name, "Неизвестное Activity: ${this.javaClass.name}")
    }
}