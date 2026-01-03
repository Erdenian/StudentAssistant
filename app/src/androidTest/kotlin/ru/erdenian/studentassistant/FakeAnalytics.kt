package ru.erdenian.studentassistant

import ru.erdenian.studentassistant.analytics.api.Analytics

class FakeAnalytics : Analytics {

    data class Event(val name: String, val params: Map<String, Any>)

    private val _events = mutableListOf<Event>()
    val events: List<Event> get() = _events

    override fun logEvent(name: String, params: Map<String, Any>) {
        _events.add(Event(name, params))
    }
}
