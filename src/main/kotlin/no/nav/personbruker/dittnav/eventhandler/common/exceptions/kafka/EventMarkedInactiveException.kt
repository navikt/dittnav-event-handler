package no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka

import no.nav.personbruker.dittnav.eventhandler.common.exceptions.EventCacheException

class EventMarkedInactiveException(message: String, cause: Throwable?) : EventCacheException(message, cause) {
    constructor(message: String) : this(message, null)
}
