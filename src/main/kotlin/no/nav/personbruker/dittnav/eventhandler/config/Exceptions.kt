package no.nav.personbruker.dittnav.eventhandler.config

open class EventCacheException(message: String, cause: Throwable?) : Exception(message, cause) {

    private val context: MutableMap<String, Any> = mutableMapOf()

    fun addContext(key: String, value: Any) {
        context[key] = value
    }

    override fun toString(): String {
        return when (context.isNotEmpty()) {
            true -> super.toString() + ", context: $context"
            false -> super.toString()
        }
    }
}


class BackupEventException(message: String, cause: Throwable?) : EventCacheException(message, cause)

class UnretriableDatabaseException(message: String, cause: Throwable? = null) : EventCacheException(message, cause)

class RetriableDatabaseException(message: String, cause: Throwable? = null) : EventCacheException(message, cause)