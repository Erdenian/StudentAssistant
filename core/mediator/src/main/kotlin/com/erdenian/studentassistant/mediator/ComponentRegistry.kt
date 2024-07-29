package com.erdenian.studentassistant.mediator

import kotlin.reflect.KClass

object ComponentRegistry {

    private val creators: MutableMap<KClass<*>, () -> ApiComponentHolder<*>> = mutableMapOf()
    private val instances: MutableMap<KClass<*>, ApiComponentHolder<*>> = mutableMapOf()

    fun register(apiClass: KClass<*>, creator: () -> ApiComponentHolder<*>) {
        creators[apiClass] = creator
    }

    fun findOrThrow(apiClass: KClass<*>): ApiComponentHolder<*> =
        instances.getOrPut(apiClass) { creators.getValue(apiClass).invoke() }
}

inline fun <reified Api, reified T : ApiComponentHolder<Api>> componentRegistry(noinline creator: () -> ApiComponentHolder<*>): Lazy<T> {
    ComponentRegistry.register(Api::class, creator)
    return lazy { ComponentRegistry.findOrThrow(Api::class) as T }
}

inline fun <reified Api, reified T : ApiComponent<Api>> findComponent(): T =
    ComponentRegistry.findOrThrow(Api::class).component as T
