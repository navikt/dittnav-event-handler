package no.nav.personbruker.dittnav.eventhandler.config

open class EventCacheException(message: String, cause: Throwable?, ident: String?) : Exception(message, cause) {

    private val context: MutableMap<String, Any> = mutableMapOf()

    init {
        ident?.let {
            context["ident"] to ident
        }
    }

    fun addContext(key: String, value: Any) {
        context[key] = value
    }

    fun securelogMessage() =
        when (context.isNotEmpty()) {
            true -> super.toString() + ", context: $context"
            false -> super.toString()
        }
}

class UnretriableDatabaseException(message: String, cause: Throwable? = null, ident: String? = null) :
    EventCacheException(message, cause, ident)

class RetriableDatabaseException(message: String, cause: Throwable? = null, ident: String? = null) :
    EventCacheException(message, cause, ident)