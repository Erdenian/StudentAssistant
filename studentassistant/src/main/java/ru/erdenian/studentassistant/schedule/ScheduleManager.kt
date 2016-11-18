package ru.erdenian.studentassistant.schedule

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.joda.time.LocalDate
import ru.erdenian.gsonguavadeserializers.ImmutableListDeserializer
import ru.erdenian.gsonguavadeserializers.ImmutableSortedSetDeserializer
import ru.erdenian.studentassistant.ulils.FileUtils
import java.io.*
import java.util.*

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
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
                    val jsonReader = InputStreamReader(FileInputStream(FileUtils.getScheduleFile()), "UTF-8")
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
        set(value) {
            //Todo: код, создающий патчи

            val semestersOld = semesters
            field = value

            currentSemesterIndex = null

            // Запись в файл
            try {
                FileUtils.getJsonFolder().mkdirs()
                FileUtils.getScheduleFile().createNewFile()
                val jsonWriter = OutputStreamWriter(FileOutputStream(FileUtils.getScheduleFile()), "UTF-8")
                Converters.registerAll(GsonBuilder()).create().toJson(semesters, jsonWriter)
                jsonWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Поиск нового индекса выбранного семестра
            selectedSemesterIndex = currentSemesterIndex

            val selectedSemesterIndexLocal = selectedSemesterIndex
            if ((selectedSemesterIndexLocal != null) && semestersOld.isNotEmpty()) {
                val previousSelectedSemesterId = semestersOld.asList()[selectedSemesterIndexLocal].id
                for ((i, semester) in semesters.withIndex())
                    if (semester.id == previousSelectedSemesterId)
                        selectedSemesterIndex = i
            }

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
            if (value !in semesters.indices) throw IllegalArgumentException("Неверный индекс: $value")
            field = value
        }

    private var onScheduleUpdateListener: OnScheduleUpdateListener? = null
    fun setOnScheduleUpdateListener(value: OnScheduleUpdateListener?) {
        onScheduleUpdateListener = value
    }

    fun getSemesterIndex(id: Long): Int? {
        for ((i, semester) in semesters.withIndex())
            if (semester.id == id) return i
        return null
    }

    fun getSemestersNames(): List<String> {
        val names = ArrayList<String>()
        for ((name) in semesters) {
            names.add(name)
        }
        return names
    }

    fun getCurrentSemester(): Semester? {
        val currentSemesterIndexLocal = currentSemesterIndex
        return if (currentSemesterIndexLocal != null) semesters.asList()[currentSemesterIndexLocal] else null
    }

    fun removeSemester(i: Int) {
        if (i !in semesters.indices) throw IllegalArgumentException("Неверный индекс: $i")
        semesters = ImmutableSortedSet.copyOf(semesters.filterIndexed { position, semester -> position != i })
    }

    fun removeSemester(id: Long) {
        semesters = ImmutableSortedSet.copyOf(semesters.filter { it.id != id })
    }
}
