package com.erdenian.studentassistant.mediator

abstract class Mediator<Api> {

    protected abstract val apiComponentHolder: ApiComponentHolder<Api>

    val api: Api get() = apiComponentHolder.api
}
