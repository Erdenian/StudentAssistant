package ru.erdenian.studentassistant.utils

/**
 * Преобразует многострочный текст в единственную строку.
 *
 * @receiver текст
 * @return тот же текст, но в одну строку
 * @author Ilya Solovyov
 * @since 0.2.6
 */
fun String.toSingleLine() = lineSequence().joinToString(" ")
