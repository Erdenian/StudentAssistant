package ru.erdenian.studentassistant

import ru.erdenian.studentassistant.analytics.api.Analytics

class FakeAnalytics : Analytics {

    data class Event(val name: String, val params: Map<String, Any>)

    private val _events = mutableListOf<Event>()
    val events: List<Event> get() = _events

    private val _userProperties = mutableMapOf<String, String?>()
    val userProperties: Map<String, String?> get() = _userProperties

    override fun logEvent(name: String, params: Map<String, Any>) {
        _events.add(Event(name, params))
    }

    override fun setUserProperty(name: String, value: String?) {
        _userProperties[name] = value
    }
}
