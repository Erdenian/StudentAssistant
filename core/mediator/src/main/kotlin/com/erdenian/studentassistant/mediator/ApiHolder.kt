package com.erdenian.studentassistant.mediator

open class ApiHolder<Api>(
    val component: ApiProvider<Api>,
) : ApiProvider<Api> by component

interface ApiProvider<Api> {
    val api: Api
}
