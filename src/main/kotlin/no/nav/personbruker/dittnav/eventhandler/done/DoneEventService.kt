package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.eventhandler.beskjed.getBeskjedByIds
import no.nav.personbruker.dittnav.eventhandler.beskjed.setBeskjedInaktiv
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import org.slf4j.LoggerFactory

class DoneEventService(private val database: Database) {
    val log = LoggerFactory.getLogger(DoneEventService::class.java)
    suspend fun markEventAsInaktiv(innloggetBruker: TokenXUser, eventId: String) {
        database.dbQuery {
            setBeskjedInaktiv(fodselsnummer = innloggetBruker.ident, eventId = eventId)
        }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedDoneEventsByProducer() }
    }

    suspend fun getNumberOfInactiveBrukernotifikasjonerByProducer(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation {
            countTotalNumberPerProducerByActiveStatus(false)
        }
    }
}
