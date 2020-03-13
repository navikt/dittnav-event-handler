package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.NoEventsException
import no.nav.personbruker.dittnav.eventhandler.config.isOtherEnvironmentThanProd

class DoneEventService(private val database: Database) {

    suspend fun markEventAsDone(innloggetBruker: InnloggetBruker, doneDto: Done) {
        if(isOtherEnvironmentThanProd()) {
            val eventBeskjedListe = getBeskjedFromCacheForUser(innloggetBruker.ident, doneDto.uid, doneDto.eventId)
            isEventBeskjedListValid(eventBeskjedListe)
            DoneProducer.produceDoneEventForSuppliedEventId(innloggetBruker.ident, doneDto.eventId, eventBeskjedListe.first())
        } else {
            throw Exception("Funksjonaliteten er ikke tilgjengelig i dette miljøet.")
        }

    }

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, uid: String, eventId: String): List<Beskjed> {
        return database.dbQuery {
            getActiveBeskjedByIds(fodselsnummer, uid, eventId)
        }
    }

    fun isEventBeskjedListValid(events: List<Beskjed>) {
        if (events.isEmpty()) {
            throw NoEventsException("Listen(beskjed) var tom.")
        } else if (events.size > 1) {
            throw DuplicateEventException("Producer: ${events.first().produsent}, ListSize: ${events.size}")
        }
    }

}
