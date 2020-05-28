package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.getActiveBeskjedByIds
import no.nav.personbruker.dittnav.eventhandler.beskjed.getAllInactiveBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException

class DoneEventService(private val database: Database, private val doneProducer: DoneProducer) {

    suspend fun markEventAsDone(innloggetBruker: InnloggetBruker, doneDto: Done) {
        val eventBeskjedListe = getBeskjedFromCacheForUser(innloggetBruker.ident, doneDto.uid, doneDto.eventId)
        isEventBeskjedListValid(eventBeskjedListe)
        doneProducer.produceDoneEventForSuppliedEventId(innloggetBruker.ident, doneDto.eventId, eventBeskjedListe.first())
    }

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, uid: String, eventId: String): List<Beskjed> {
        var result = emptyList<Beskjed>()
        database.queryWithExceptionTranslation {
            result = getActiveBeskjedByIds(fodselsnummer, uid, eventId)
        }
        return result
    }

    fun isEventBeskjedListValid(events: List<Beskjed>) {
        if (events.isEmpty()) {
            throw NoEventsException("Listen(beskjed) var tom.")
        } else if (events.size > 1) {
            throw DuplicateEventException("Producer: ${events.first().produsent}, ListSize: ${events.size}")
        }
    }

    suspend fun produceDoneEventsFromBeskjedEvents(): List<Beskjed> {
        val allDoneEventsFromBeskjedEvents = getAllInactiveBeskjedEventsFromCache()
        if (allDoneEventsFromBeskjedEvents.isNotEmpty()) {
            doneProducer.produceDoneEventsFromList(allDoneEventsFromBeskjedEvents)
        }
        return allDoneEventsFromBeskjedEvents
    }

    suspend fun getAllInactiveBeskjedEventsFromCache(): List<Beskjed> {
        var result = emptyList<Beskjed>()
        database.queryWithExceptionTranslation {
            result = getAllInactiveBeskjed()
        }
        return result
    }

}
