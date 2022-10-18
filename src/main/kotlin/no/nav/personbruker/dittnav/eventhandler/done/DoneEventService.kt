package no.nav.personbruker.dittnav.eventhandler.done

import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.beskjed.setBeskjedInaktiv
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser

class DoneEventService(private val database: Database) {

    val log = KotlinLogging.logger {}

    suspend fun markEventAsInaktiv(fnr: String, eventId: String) {
        database.dbQuery {
            setBeskjedInaktiv(fodselsnummer = fnr, eventId = eventId).also {
                if(it==0){
                    log.warn ("Forsøk på inaktiv-markering av varsel med eventid $eventId påvirket 0 rader")
                }
            }
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
