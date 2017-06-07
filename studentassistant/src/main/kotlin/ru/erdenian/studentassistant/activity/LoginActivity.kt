package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_login.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.netty.nettyQuery


class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    content_login_login_edit_text.addTextChangedListener(object : TextWatcher {

      override fun afterTextChanged(s: Editable?) {
        if (!s.isNullOrBlank())
          nettyQuery(",:checklogin:${s!!.trim()}") {
            runOnUiThread {
              if (it.toBoolean()) {
                content_login_login.isErrorEnabled = true
                content_login_login.error = "Логин занят"
              } else {
                content_login_login.isErrorEnabled = false
              }
            }
          }
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    content_login_sign_in.setOnClickListener {
      val login = content_login_login_edit_text.text.trim().toString()
      val password = content_login_password_edit_text.text.toString()

      nettyQuery(",:checkuser:$login,$password") {
        if (it.toBoolean()) {
          defaultSharedPreferences.edit().apply {
            putString("login", login)
            putString("password", password)
          }.apply()
          finish()
        } else {
          toast("Неверный логин или пароль")
        }
      }
    }

    content_login_sign_up.setOnClickListener {
      if (content_login_login_edit_text.text.isNullOrBlank()) {
        toast("Введите логин")
      } else if (content_login_password_edit_text.text.isNullOrBlank()) {
        toast("Введите пароль")
      } else {
        nettyQuery(",:registration:${content_login_login_edit_text.text},${content_login_password_edit_text.text}") {
          defaultSharedPreferences.edit().apply {
            putString("login", content_login_login_edit_text.text.trim().toString())
            putString("password", content_login_password_edit_text.text.toString())
          }.apply()
        }
        finish()
      }
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
