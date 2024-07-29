package com.erdenian.studentassistant.mediator

abstract class Mediator<Api> {

    abstract val apiHolder: ApiHolder<Api>

    val api: Api get() = apiHolder.api
}
