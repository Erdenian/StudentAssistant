package ru.erdenian.studentassistant.extensions

import android.app.Activity
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.navigation_view.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.*
import ru.erdenian.studentassistant.schedule.ScheduleManager

fun Activity.initializeDrawerAndNavigationView(toolbar: Toolbar) {
  val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

  val toggle = ActionBarDrawerToggle(
      this, drawerLayout, toolbar,
      R.string.navigation_drawer_open, R.string.navigation_drawer_close)
  drawerLayout.addDrawerListener(toggle)
  toggle.syncState()

  navigation_view.getHeaderView(0).let {
    it.findViewById(R.id.navigation_view_header_sign_in).setOnClickListener {
      startActivity<LoginActivity>()
    }
    (it.findViewById(R.id.navigation_view_header_name) as TextView).text = ""
    (it.findViewById(R.id.navigation_view_header_group) as TextView).text = ""
    (it.findViewById(R.id.navigation_view_header_sign_in) as Button).text = "Войти"

    val i = it

    defaultSharedPreferences.apply {
      val login = getString("login", null)
      val password = getString("password", null)

      login?.let {
        (i.findViewById(R.id.navigation_view_header_name) as TextView).text = login
        (i.findViewById(R.id.navigation_view_header_group) as TextView).text = ""
        (i.findViewById(R.id.navigation_view_header_sign_in) as Button).text = "Профиль"

        (i.findViewById(R.id.navigation_view_header_sign_in) as Button).setOnClickListener {
          startActivity<UserDetailsActivity>()
        }
      }
    }
  }


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
      R.id.nav_help -> startActivity<HelpActivity>()
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