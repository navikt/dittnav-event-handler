package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<Innboks> {
        return getEvents {
            getAktivInnboksForFodselsnummer(fodselsnummer)
        }
    }

    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<Innboks> {
        return getEvents {
            getInaktivInnboksForFodselsnummer(fodselsnummer)
        }
    }

    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<Innboks> {
        return getEvents {
            getAllInnboksForFodselsnummer(fodselsnummer)
        }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedInnboksEventsByProducer() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Innboks>): List<Innboks> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
