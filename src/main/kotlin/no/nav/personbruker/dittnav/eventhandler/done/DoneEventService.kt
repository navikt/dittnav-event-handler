package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.getBeskjedByIds
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.EventMarkedInactiveException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser

class DoneEventService(private val database: Database, private val doneProducer: DoneProducer) {

    suspend fun markEventAsDone(innloggetBruker: TokenXUser, doneDTODto: DoneDTO) {
        val beskjedEvent = getBeskjedFromCacheForUser(innloggetBruker.ident, doneDTODto.uid, doneDTODto.eventId)
        doneProducer.produceDoneEventForSuppliedEventId(innloggetBruker.ident, doneDTODto.eventId, beskjedEvent)
    }

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, uid: String, eventId: String): Beskjed {
        val result: List<Beskjed> = database.queryWithExceptionTranslation {
             getBeskjedByIds(fodselsnummer, uid, eventId)
        }
        return validBeskjed(result)
    }

    suspend fun getAllGroupedEventsBySystemuserFromCache(): Map<String, Int> {
        return database.queryWithExceptionTranslation { getAllGroupedDoneEventsBySystemuser() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedDoneEventsByProducer() }
    }

    suspend fun getNumberOfInactiveBrukernotifikasjonerGroupedBySystemuser(): Map<String, Int> {
        return database.queryWithExceptionTranslation {
            countTotalNumberOfBrukernotifikasjonerByActiveStatus(false)
        }
    }

    suspend fun getNumberOfInactiveBrukernotifikasjonerByProducer(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation {
            countTotalNumberPerProducerByActiveStatus(false)
        }
    }

    fun validBeskjed(events: List<Beskjed>): Beskjed {
        if (events.isEmpty()) {
            throw NoEventsException("Listen(beskjed) var tom.")
        } else if (events.size > 1) {
            throw DuplicateEventException("Appnavn: ${events.first().appnavn}, ListSize: ${events.size}")
        } else if (!events.first().aktiv) {
            throw EventMarkedInactiveException("Tilhørende event er allerede markert done.")
        }

        return events.first()
    }
}
