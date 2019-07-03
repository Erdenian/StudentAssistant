package ru.erdenian.studentassistant.utils

private val lineSeparator = System.getProperty("line.separator")

/**
 * Преобразует многострочный текст в единственную строку.
 *
 * @author Ilya Solovyev
 * @since 0.2.6
 * @receiver текст
 * @return тот же текст, но в одну строку
 */
fun String.toSingleLine() = replace(lineSeparator, " ")
