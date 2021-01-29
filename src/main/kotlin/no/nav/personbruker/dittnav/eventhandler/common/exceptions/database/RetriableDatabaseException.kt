package no.nav.personbruker.dittnav.eventhandler.common.exceptions.database

import no.nav.personbruker.dittnav.eventhandler.common.exceptions.EventCacheException

class RetriableDatabaseException(message: String, cause: Throwable?) : EventCacheException(message, cause) {
    constructor(message: String) : this(message, null)
}
