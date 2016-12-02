package ru.erdenian.studentassistant.schedule

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.joda.time.LocalDate
import ru.erdenian.gsonguavadeserializers.ImmutableListDeserializer
import ru.erdenian.gsonguavadeserializers.ImmutableSortedSetDeserializer
import ru.erdenian.studentassistant.extensions.addToNewSet
import ru.erdenian.studentassistant.extensions.replaceToNewSet
import ru.erdenian.studentassistant.ulils.FileUtils
import java.io.*
import java.util.*

object ScheduleManager {

    private var isInitialized = false
    var semesters: ImmutableSortedSet<Semester> = ImmutableSortedSet.of()
        get(): ImmutableSortedSet<Semester> {
            if (!isInitialized) {
                // Чтение из файла
                try {
                    val gson = Converters.registerAll(GsonBuilder())
                            .registerTypeAdapter(ImmutableSortedSet::class.java, ImmutableSortedSetDeserializer())
                            .registerTypeAdapter(ImmutableList::class.java, ImmutableListDeserializer())
                            .create()
                    val jsonReader = InputStreamReader(FileInputStream(FileUtils.scheduleFile), "UTF-8")
                    val type = object : TypeToken<ImmutableSortedSet<Semester>>() {}.type
                    field = gson.fromJson<ImmutableSortedSet<Semester>>(jsonReader, type)
                } catch (e: FileNotFoundException) {
                    field = ImmutableSortedSet.of<Semester>()
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                currentSemesterIndex = null
                isInitialized = true
            }
            return field
        }
        private set(value) {
            field = value

            currentSemesterIndex = null
            selectedSemesterIndex = null

            // Запись в файл
            try {
                FileUtils.jsonFolder.mkdirs()
                FileUtils.scheduleFile.createNewFile()
                val jsonWriter = OutputStreamWriter(FileOutputStream(FileUtils.scheduleFile), "UTF-8")
                Converters.registerAll(GsonBuilder()).create().toJson(semesters, jsonWriter)
                jsonWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            isInitialized = true

            onScheduleUpdateListener?.onScheduleUpdate()
        }

    var currentSemesterIndex: Int? = null
        get() {
            if ((field == null) && semesters.isNotEmpty()) {
                field = semesters.size - 1

                val today = LocalDate.now()
                for ((i, semester) in semesters.withIndex()) {
                    if (!today.isBefore(semester.firstDay) && !today.isAfter(semester.lastDay)) {
                        field = i
                        break
                    }
                }
            }
            return field
        }
        private set

    var selectedSemesterIndex: Int? = null
        get() {
            if (field == null) field = currentSemesterIndex
            return field
        }
        set(value) {
            if ((value !in semesters.indices) && (value != null)) throw IllegalArgumentException("Неверный индекс: $value")
            field = value
        }

    private var onScheduleUpdateListener: OnScheduleUpdateListener? = null
    fun setOnScheduleUpdateListener(value: OnScheduleUpdateListener?) {
        onScheduleUpdateListener = value
    }

    operator fun get(i: Int): Semester = semesters.asList()[i]

    operator fun get(id: Long): Semester? {
        return semesters.asList()[getSemesterIndex(id) ?: return null]
    }

    fun getSemesterIndex(id: Long): Int? {
        for ((i, semester) in semesters.withIndex())
            if (semester.id == id) return i
        return null
    }

    val semestersNames: List<String>
        get() {
            val names = ArrayList<String>()
            for ((name) in semesters) {
                names.add(name)
            }
            return names
        }

    val currentSemester: Semester?
        get() {
            val currentSemesterIndexLocal = currentSemesterIndex
            return if (currentSemesterIndexLocal != null) semesters.asList()[currentSemesterIndexLocal] else null
        }

    val selectedSemester: Semester?
        get() {
            val selectedSemesterIndexLocal = selectedSemesterIndex
            return if (selectedSemesterIndexLocal != null) semesters.asList()[selectedSemesterIndexLocal] else null
        }

    fun removeSemester(i: Int) {
        //Todo: код, создающий патчи

        if (i !in semesters.indices) throw IllegalArgumentException("Неверный индекс: $i")
        semesters = ImmutableSortedSet.copyOf(semesters.filterIndexed { position, semester -> position != i })
    }

    fun removeSemester(id: Long) {
        //Todo: код, создающий патчи

        removeSemester(getSemesterIndex(id) ?: throw IllegalArgumentException("Неверный id: $id"))
    }

    fun addSemester(semester: Semester) {
        //Todo: код, создающий патчи

        val index = getSemesterIndex(semester.id)
        if (index == null) {
            semesters = semesters.addToNewSet(semester)
            selectedSemesterIndex = getSemesterIndex(semester.id)
        } else {
            val i = selectedSemesterIndex
            semesters = semesters.replaceToNewSet(get(index), semester)
            selectedSemesterIndex = i
        }
    }

    fun addLesson(semesterId: Long, lesson: Lesson) {
        //Todo: код, создающий патчи

        val semester = get(semesterId) ?: throw IllegalArgumentException("Неверный id: $semesterId")
        val oldLesson = semester.getLesson(lesson.id)
        val newSemester: Semester = if (oldLesson == null) semester.copy(lessons = semester.lessons.addToNewSet(lesson))
        else semester.copy(lessons = semester.lessons.replaceToNewSet(oldLesson, lesson))

        semesters = semesters.replaceToNewSet(semester, newSemester)
    }

    fun removeLesson(semesterId: Long, lessonId: Long) {
        //Todo: код, создающий патчи

        val semester = get(semesterId) ?: throw IllegalArgumentException("Неверный id семестра: $semesterId")
        val newLessons = ImmutableSortedSet.copyOf(semester.lessons.filter { it.id != lessonId })
        val newSemester = semester.copy(lessons = newLessons)

        semesters = semesters.replaceToNewSet(semester, newSemester)
    }
}
