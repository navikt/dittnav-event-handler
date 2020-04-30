package no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka

import no.nav.personbruker.dittnav.eventhandler.common.exceptions.EventCacheException

class DuplicateEventException(message: String, cause: Throwable?) : EventCacheException(message, cause) {
    constructor(message: String) : this(message, null)
}
