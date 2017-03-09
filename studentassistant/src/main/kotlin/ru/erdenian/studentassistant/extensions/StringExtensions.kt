package ru.erdenian.studentassistant.extensions

val String.asSingleLine: String
  get() = replace(System.getProperty("line.separator"), " ")