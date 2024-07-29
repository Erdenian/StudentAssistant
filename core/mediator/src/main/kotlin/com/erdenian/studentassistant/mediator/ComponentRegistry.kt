package com.erdenian.studentassistant.mediator

import kotlin.reflect.KClass

object ComponentRegistry {

    private val creators: MutableMap<KClass<*>, () -> ApiHolder<*>> = mutableMapOf()
    private val instances: MutableMap<KClass<*>, ApiHolder<*>> = mutableMapOf()

    fun register(apiClass: KClass<*>, creator: () -> ApiHolder<*>) {
        creators[apiClass] = creator
    }

    fun findOrThrow(apiClass: KClass<*>): ApiHolder<*> = instances.getOrPut(apiClass) { creators.getValue(apiClass).invoke() }
}

inline fun <reified Api, reified T : ApiHolder<Api>> componentRegistry(noinline creator: () -> ApiHolder<*>): Lazy<T> {
    ComponentRegistry.register(Api::class, creator)
    return lazy { ComponentRegistry.findOrThrow(Api::class) as T }
}

inline fun <reified Api, reified T : ApiProvider<Api>> findComponent(): T =
    ComponentRegistry.findOrThrow(Api::class).component as T
