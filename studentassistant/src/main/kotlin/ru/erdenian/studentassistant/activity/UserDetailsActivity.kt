package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_user_details.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.defaultSharedPreferences
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.netty.nettyQuery


class UserDetailsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_user_details)

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    defaultSharedPreferences.let {
      content_user_details_university_edit_text.setText(it.getString("university", ""))
      content_user_details_faculty_edit_text.setText(it.getString("faculty", ""))
      content_user_details_group_edit_text.setText(it.getString("group", ""))

      nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::getuniversities::") {
        val universities = it.split(",")
        runOnUiThread {
          content_user_details_university_edit_text.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, universities))
        }
      }
      nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::getfaculties::1") {
        val faculties = it.split(",")
        runOnUiThread {
          content_user_details_faculty_edit_text.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, faculties))
        }
      }
      nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::getgroups::1") {
        val groups = it.split(",")
        runOnUiThread {
          content_user_details_group_edit_text.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, groups))
        }
      }
    }

    content_user_details_save.setOnClickListener {
      defaultSharedPreferences.edit().apply {
        putString("university", content_user_details_university_edit_text.text.toString())
        putString("faculty", content_user_details_faculty_edit_text.text.toString())
        putString("group", content_user_details_group_edit_text.text.toString())
      }.apply()
      defaultSharedPreferences.let {
        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::changegroup::${content_user_details_university_edit_text.text};${content_user_details_faculty_edit_text.text};${content_user_details_group_edit_text.text}")
      }
      finish()
    }

    content_user_details_logout.setOnClickListener {
      defaultSharedPreferences.edit().apply {
        remove("login")
        remove("password")
      }.apply()
      finish()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> finish()
      else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
    }
    return super.onOptionsItemSelected(item)
  }
}
