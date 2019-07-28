package ru.erdenian.studentassistant.model.entity

/**
 * Возвращает long, который можно использовать как id.
 *
 * @return id
 * @author Ilya Solovyev
 * @since 0.2.0
 */
fun generateId() = System.currentTimeMillis()
