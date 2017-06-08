package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.content_schedule.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.netty.nettyQuery
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager

class ScheduleActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener,
    CalendarDatePickerDialogFragment.OnDateSetListener,
    View.OnClickListener,
    OnScheduleUpdateListener {

  private companion object {
    const val CURRENT_PAGE = "current_page"
    const val SELECTED_SEMESTER_ID = "selected_semester_id"
  }

  private var savedPage: Int? = null
  private var selectedSemesterId: Long? = null

  private var pagerAdapter: SchedulePagerAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_schedule)

    setSupportActionBar(toolbar_with_spinner)
    initializeDrawerAndNavigationView(toolbar_with_spinner)

    toolbar_with_spinner_spinner.onItemSelectedListener = this

    content_schedule_get_schedule_from_server.setOnClickListener(this)
    content_schedule_add_schedule.setOnClickListener(this)

    view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
    view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)
  }

  override fun onStart() {
    super.onStart()
    ScheduleManager.addOnScheduleUpdateListener(this)
    onScheduleUpdate()
  }

  override fun onResume() {
    super.onResume()
    initializeDrawerAndNavigationView(toolbar_with_spinner)
  }

  override fun onScheduleUpdate() {
    runOnUiThread {
      supportActionBar!!.setDisplayShowTitleEnabled(ScheduleManager.semesters.size <= 1)
      toolbar_with_spinner_spinner.visibility = if (ScheduleManager.semesters.size > 1) View.VISIBLE else View.GONE

      view_pager.visibility = if (ScheduleManager.semesters.isNotEmpty()) View.VISIBLE else View.GONE
      content_schedule_add_buttons.visibility = if (ScheduleManager.semesters.isEmpty()) View.VISIBLE else View.GONE

      if ((pagerAdapter != null) && (selectedSemesterId == ScheduleManager.selectedSemesterId)) {
        savedPage = view_pager.currentItem
      }
      selectedSemesterId = ScheduleManager.selectedSemesterId ?: -1

      if (ScheduleManager.semesters.size > 1) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item_semesters, ScheduleManager.semestersNames)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters)
        toolbar_with_spinner_spinner.adapter = adapter
        toolbar_with_spinner_spinner.setSelection(ScheduleManager.selectedSemesterIndex)
      } else if (ScheduleManager.semesters.size == 1) {
        supportActionBar!!.title = ScheduleManager.selectedSemester!!.name
        onItemSelected(null, null, 0, -1L)
      } else {
        supportActionBar!!.setTitle(R.string.title_activity_schedule)
        pagerAdapter = null
        view_pager.adapter = pagerAdapter
      }

      invalidateOptionsMenu()
      initializeDrawerAndNavigationView(toolbar_with_spinner)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(CURRENT_PAGE, view_pager.currentItem)
    outState.putLong(SELECTED_SEMESTER_ID, selectedSemesterId ?: -1L)
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    savedPage = savedInstanceState.getInt(CURRENT_PAGE, -1).takeIf { it != -1 }
    selectedSemesterId = savedInstanceState.getLong(SELECTED_SEMESTER_ID, -1L).takeIf { it != -1L }
    super.onRestoreInstanceState(savedInstanceState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_schedule, menu)

    val isSemestersEmpty = ScheduleManager.semesters.isEmpty()
    menu.findItem(R.id.menu_schedule_calendar).isVisible = !isSemestersEmpty
    menu.findItem(R.id.menu_schedule_edit_schedule).isVisible = !isSemestersEmpty

    menu.setColor(getCompatColor(R.color.action_bar_icons_color))
    return true
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    selectedSemesterId = ScheduleManager.semesters.asList()[position].id
    ScheduleManager.selectedSemesterId = selectedSemesterId

    pagerAdapter = SchedulePagerAdapter(supportFragmentManager, ScheduleManager.selectedSemester!!, false)
    view_pager.adapter = pagerAdapter
    view_pager.setCurrentItem(if (savedPage != null) savedPage!! else pagerAdapter!!.getPosition(LocalDate.now()), false)
    savedPage = null
  }

  override fun onNothingSelected(parent: AdapterView<*>) = Unit

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_schedule_calendar -> {
        val selectedSemester = ScheduleManager.selectedSemester!!
        showDatePicker(this, selectedSemester.firstDay, selectedSemester.lastDay,
            pagerAdapter!!.getDate(view_pager.currentItem))
      }
      R.id.menu_schedule_add_schedule -> startActivity<SemesterEditorActivity>()
      R.id.menu_schedule_edit_schedule ->
        startActivity<LessonsEditorActivity>(SEMESTER_ID to ScheduleManager.selectedSemesterId!!)
      R.id.menu_schedule_sync_schedule -> {
        val semester = ScheduleManager.getSemester(ScheduleManager.semesterToSyncId)
        val semesterJson = Gson().toJson(semester)
        val lessons = ScheduleManager.getLessons(ScheduleManager.semesterToSyncId)
        val lessonsJson = Gson().toJson(lessons)
        defaultSharedPreferences.let {
          nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::getschedule::") {
            TODO()
          }
        }
      }
      else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDateSet(dialog: CalendarDatePickerDialogFragment, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    view_pager.currentItem = pagerAdapter!!.getPosition(LocalDate(year, monthOfYear + 1, dayOfMonth))
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.content_schedule_get_schedule_from_server -> toast(R.string.content_schedule_get_schedule_from_server_button)
      R.id.content_schedule_add_schedule -> startActivity<SemesterEditorActivity>()
      else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
    }
  }

  override fun onBackPressed() {
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }
}
