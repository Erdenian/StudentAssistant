package ru.erdenian.studentassistant.extensions

private val lineSeparator = System.getProperty("line.separator")

val String.asSingleLine get() = replace(lineSeparator, " ")
