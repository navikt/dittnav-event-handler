package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.getBeskjedByIds
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.EventMarkedInactiveException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException

class DoneEventService(private val database: Database, private val doneProducer: DoneProducer) {

    suspend fun markEventAsDone(innloggetBruker: InnloggetBruker, doneDto: Done) {
        val beskjedEvent = getBeskjedFromCacheForUser(innloggetBruker.ident, doneDto.uid, doneDto.eventId)
        doneProducer.produceDoneEventForSuppliedEventId(innloggetBruker.ident, doneDto.eventId, beskjedEvent)
    }

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, uid: String, eventId: String): Beskjed {
        var result = emptyList<Beskjed>()
        database.queryWithExceptionTranslation {
            result = getBeskjedByIds(fodselsnummer, uid, eventId)
        }
        return validBeskjed(result)
    }


    fun validBeskjed(events: List<Beskjed>): Beskjed {
        if (events.isEmpty()) {
            throw NoEventsException("Listen(beskjed) var tom.")
        } else if (events.size > 1) {
            throw DuplicateEventException("Producer: ${events.first().produsent}, ListSize: ${events.size}")
        } else if (!events.first().aktiv) {
            throw EventMarkedInactiveException("Tilhørende event er allerede markert done.")
        }

        return events.first()
    }
}
