package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class DoneEventService(private val database: Database) {

    fun markEventAsDone(fodselsnummer: String, eventId : String) {
        DoneProducer.produceDoneEventForSuppliedEventId(fodselsnummer, eventId)
    }
}