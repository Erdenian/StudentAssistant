package ru.erdenian.studentassistant.extensions

import android.content.Context
import android.support.v4.content.ContextCompat
import java.io.File

fun Context.getCompatColor(id: Int) = ContextCompat.getColor(this, id)

fun Context.clearApplicationData() {

  fun deleteFile(file: File) {
    if (file.isDirectory) file.list().forEach { deleteFile(File(file, it)) }
    else file.delete()
  }

  val applicationDirectory = File(cacheDir.parent)
  if (applicationDirectory.exists())
    applicationDirectory.list().filter { it != "lib" }.forEach { deleteFile(File(applicationDirectory, it)) }
}
