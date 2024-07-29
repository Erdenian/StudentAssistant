package com.erdenian.studentassistant.mediator

open class ApiComponentHolder<Api>(
    val component: ApiComponent<Api>,
) : ApiComponent<Api> by component

interface ApiComponent<Api> {
    val api: Api
}
