package no.nav.personbruker.dittnav.eventhandler.common.exceptions

class FieldValidationException(message: String, cause: Throwable?) : EventCacheException(message, cause) {
    constructor(message: String) : this(message, null)
}