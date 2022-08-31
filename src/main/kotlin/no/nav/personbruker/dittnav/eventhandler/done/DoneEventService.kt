package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.getBeskjedByIds
import no.nav.personbruker.dittnav.eventhandler.beskjed.setBeskjedInaktiv
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.EventMarkedInactiveException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser

class DoneEventService(private val database: Database, private val doneProducer: DoneProducer) {

    suspend fun markEventAsInaktiv(innloggetBruker: TokenXUser, eventId: String) {
        database.queryWithExceptionTranslation {
            setBeskjedInaktiv(innloggetBruker.ident, eventId)
        }
    }

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, eventId: String): Beskjed {
        val result: List<Beskjed> = database.queryWithExceptionTranslation {
            getBeskjedByIds(fodselsnummer, eventId)
        }
        return validBeskjed(result)
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedDoneEventsByProducer() }
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
