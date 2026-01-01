package ru.erdenian.studentassistant.utils

import java.util.concurrent.atomic.AtomicReference

/**
 * Базовый класс для хранения Dagger компонентов.
 *
 * Позволяет создавать компонент лениво, получать к нему доступ и, главное,
 * очищать ссылку на него для освобождения памяти.
 *
 * @param C тип компонента.
 * @param D тип зависимостей, необходимых для создания компонента.
 * @param factory функция создания компонента.
 */
open class BaseComponentHolder<C : Any, D : Any>(
    private val factory: (D) -> C,
) {

    private val instanceRef = AtomicReference<C?>(null)

    /**
     * Возвращает текущий экземпляр компонента.
     *
     * @throws IllegalStateException если компонент еще не был инициализирован.
     */
    val instance: C
        get() = checkNotNull(instanceRef.get()) { "Component is not initialized. Call create() first." }

    /**
     * Проверяет, инициализирован ли компонент.
     */
    val isInitialized: Boolean
        get() = instanceRef.get() != null

    /**
     * Создает компонент, если он еще не создан, или возвращает существующий.
     *
     * Метод потокобезопасен.
     */
    fun create(dependencies: D): C {
        val current = instanceRef.get()
        if (current != null) return current

        synchronized(this) {
            val synchronizedCurrent = instanceRef.get()
            if (synchronizedCurrent != null) return synchronizedCurrent

            val newInstance = factory(dependencies)
            instanceRef.set(newInstance)
            return newInstance
        }
    }

    /**
     * Очищает ссылку на компонент.
     *
     * Следует вызывать, когда фича закрывается и граф зависимостей больше не нужен.
     */
    fun clear() {
        instanceRef.set(null)
    }
}
