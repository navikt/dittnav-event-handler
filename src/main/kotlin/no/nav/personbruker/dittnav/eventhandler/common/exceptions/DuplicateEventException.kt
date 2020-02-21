package no.nav.personbruker.dittnav.eventhandler.common.exceptions

import java.lang.Exception

class DuplicateEventException(message: String, cause: Throwable?) : Exception() {
    constructor(message: String) : this(message, null)
}