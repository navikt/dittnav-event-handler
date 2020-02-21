package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.NoEventsException

class DoneEventService(private val database: Database) {

    suspend fun markEventAsDone(innloggetBruker: InnloggetBruker, doneDto: Done) {
        val eventBeskjedListe = getBeskjedFromCacheForUser(innloggetBruker.getIdent(), doneDto.uid, doneDto.eventId)
        isEventBeskjedListValid(eventBeskjedListe)
        DoneProducer.produceDoneEventForSuppliedEventId(innloggetBruker.getIdent(),doneDto.eventId, eventBeskjedListe.first())
    }

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, uid: String, eventId: String): List<Beskjed> {
        return database.dbQuery {
            getActiveBeskjedByIds(fodselsnummer, uid, eventId)
        }
    }

    private fun isEventBeskjedListValid(events: List<Beskjed>) {
        if (events.isEmpty()) {
          throw NoEventsException("Det ble ikke produsert et done-event fordi vi fant ikke eventet i cachen.")
        }
        if (events.size > 1) {
            throw DuplicateEventException("Det ble ikke produsert done-event fordi det finnes duplikat av events. Produsent: ${events.first().produsent}, Str beskjed liste(skal være maks 1): ${events.size}")
        }
    }
}