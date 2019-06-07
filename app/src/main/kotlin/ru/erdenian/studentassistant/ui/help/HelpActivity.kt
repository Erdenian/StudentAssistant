package ru.erdenian.studentassistant.ui.help

import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.erdenian.studentassistant.R

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<TextView>(R.id.ahlp_help).text = Html.fromHtml(
            """
            <div>
            <h1><strong>Student Assistant | Документация</strong></h1>
            <h1>Работа с расписаниями</h1>
            <p>Следующие пункты дадут вам общее представление об основных принципах работы с расписаниями в программе “Student Assistant”.
            </p>
            <p>Расписание – это график, содержащий сведения о времени, месте и последовательности совершения чего- либо.
            </p>
            <h2>Создание расписания.</h2>
            <p>Начало полноценной работы с приложением “Student Assistant” предполагает собой создание расписания (контейнера), в котором будут храниться занятия (уроки, пары и т.п.).
            </p>
            <p>Для этого:
            </p>
            <p>1) Если расписание создается впервые:
            </p>
            <p>Выберите пункт “Добавить расписание” в центре экрана или в выпадающем списке по нажатию троеточия.
            </p>
            <p>Если создается дополнительное расписание:
            </p>
            <p>Выберите пункт “Добавить расписание” в выпадающем списке по нажатию троеточия.
            </p>
            <p>2) Введите название расписания.
            </p>
            <p>3) Далее необходимо указать время начала и конца семестра (учебного полугодия и т.п.). Для этого нажмите на кнопку “ПЕРВЫЙ ДЕНЬ” или “ПОСЛЕДНИЙ ДЕНЬ”, чтобы задать дни, соответствующие названиям кнопок.
            </p>
            <p>4) Нажмите кнопку-галочку, чтобы сохранить расписание.
            </p>
            <h2>Добавление занятия</h2>
            <p>Чтобы добавить занятие, необходимо сначала создать расписание, другими словами контейнер для занятий.
            </p>
            <p>1) В окне выбранного (текущего) расписания выберите пункт “Редактировать расписание”.
            </p>
            <p>2) В окне “Редактирование расписания” нажмите кнопку “+” в правом нижнем углу экрана, чтобы перейти в окно добавления занятия.
            </p>
            <p>3) Заполните необходимые поля, предназначенные для информации о паре. Необходимыми являются “Название пары”, ” Время начала”, ” Время конца”, день недели и недели повторения занятия.
            </p>
            <p><strong>Недели повторения занятия</strong> – недели, по которым проводится создаваемое занятие.
            </p>
            <p>4) Нажмите кнопку-галочку, чтобы сохранить созданный вами предмет.
            </p>
            <h2>Редактирование занятия</h2>
            <p>В случае, если вы допустили ошибку при создании занятия или просто хотите изменить занятие, вам достаточно нажать в окне редактирования расписания на необходимую пару или в окне расписания на пару, а затем на кнопку-карандаш (доп. информация: добавление занятия ).
            </p>
            <p><em>Примечание: чтобы попасть в окно редактирования расписания, в окне выбранного (текущего) расписания выберите пункт “Редактировать расписание”.</em>
            </p>
            <h2>Редактирование расписания</h2>
            <p>1) В окне редактирования расписания выберите пункт “Редактировать расписание”. Затем откорректируйте необходимую информацию (доп. информация: создание расписания.).
            </p>
            <p>2) Нажмите кнопку-галочку, чтобы сохранить отредактированное вами расписание.
            </p>
            <p><em>Примечание: чтобы попасть в окно редактирования расписания, в окне выбранного (текущего) расписания выберите пункт “Редактировать расписание”.</em>
            </p>
            <h2>Удаление занятия</h2>
            <p>В случае, если вы хотите удалить занятие по какой- либо причине, вам необходимо выбрать в окне редактирования занятия пункт “Удалить занятие” (доп. информация: Добавление занятия, Редактирование занятия).
            </p>
            <h2>Удаление расписания</h2>
            <p>В случае, если вы хотите удалить расписание по какой- либо причине, вам необходимо выбрать в окне редактирования расписания пункт “Удалить расписание” (доп. информация: создание расписания, редактирование расписания).
            </p>
            <h2>Информация о занятии</h2>
            <p>Чтобы получить полную информацию о занятии (название, тип, время, домашние задания и т.п.), вам необходимо в окне текущего расписания нажать на необходимое вам занятие – вы перейдете в необходимое вам окно.
            </p>
            <h1>Работа с домашними заданиями</h1>
            <p>Следующие пункты дадут вам общее представление об основных принципах работы с домашними заданиями в программе “Student Assistant”.
            </p>
            <p>Перед тем как приступить к работе с домашними заданиями вы можете ознакомиться с работой с расписаниями.
            </p>
            <h2>Просмотр домашних заданий</h2>
            <p>Чтобы просмотреть список всех домашних заданий, откройте боковую панель и выберите пункт “Домашнее задание”.
            </p>
            <p>Чтобы просмотреть список домашних заданий для конкретного занятия, нажмите на занятие в окне текущего расписания.
            </p>
            <h2>Добавление домашнего задания</h2>
            <p>Добавление домашнего задания можно осуществить следующим образом:
            </p>
            <p>Перейдите в окно просмотра домашних заданий, затем нажмите кнопку-плюс.
            </p>
            <h2>Удаление домашнего задания</h2>
            <p>В окне с домашними заданиями выберите необходимое вам домашнее задание, затем выберите пункт меню “Удалить”.
            </p>
            <h2>Редактирование домашнего задания</h2>
            <p>В окне с домашними заданиями выберите необходимое вам домашнее задание.
            </p>
            <h1>Работа с будильниками</h1>
            <p>Следующая информация даст вам общее представление об основных принципах работы с будильниками в программе “Student Assistant”.
            </p>
            <p>Будильники “Student Assistant” позволяют напомнить пользователю о первом занятии за указанное время до начала занятия.
            </p>
            <p>Чтобы воспользоваться данной функцией перейдите в пункт боковой панели “Будильник”.
            </p>
            <p>Настройка будильника имеет простой интуитивный интерфейс.
            </p>
            <p>Чтобы включить будильник, укажите время до начада занятия в одноименной области, и нажатием нижней кнопки приведите будильник в активное состояние.
            </p>
            <p>Чтобы выключить будильник, нажатием нижней кнопки приведите будильник в неактивное состояние.
            </p>
            """.trimIndent().trim()
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
