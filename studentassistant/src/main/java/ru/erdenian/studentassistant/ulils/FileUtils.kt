package ru.erdenian.studentassistant.ulils

import android.content.Context

import java.io.File

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
object FileUtils {

    private const val JSON_FOLDER_PATH = "/json"
    private const val SCHEDULE_FILE_PATH = "/schedule.json"

    private lateinit var filesDirPath: String

    val filesDir: File by lazy { File(filesDirPath) }
    val jsonFolder: File by lazy { File(filesDirPath + JSON_FOLDER_PATH) }
    val scheduleFile: File by lazy { File(filesDirPath + SCHEDULE_FILE_PATH) }

    fun initialize(context: Context) {
        try {
            filesDirPath
        } catch (upae: UninitializedPropertyAccessException) {
            filesDirPath = context.filesDir.absolutePath
            return
        }
        throw UnsupportedOperationException("Объект уже инициализирован")
    }
}
